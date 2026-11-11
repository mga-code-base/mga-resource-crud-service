package com.mygym.app.idempotency;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.util.HexFormat;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class IdempotencyFilter extends OncePerRequestFilter {

    public static final String HEADER = "Idempotency-Key";
    private static final int MAX_KEY_LENGTH = 128;
    private static final int UNPROCESSABLE_CONTENT = 422;

    private final MongoTemplate mongoTemplate;
    private final Clock clock;

    @Autowired
    public IdempotencyFilter(MongoTemplate mongoTemplate) {
        this(mongoTemplate, Clock.systemUTC());
    }

    IdempotencyFilter(MongoTemplate mongoTemplate, Clock clock) {
        this.mongoTemplate = mongoTemplate;
        this.clock = clock;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String key = request.getHeader(HEADER);
        return !HttpMethod.POST.matches(request.getMethod()) || key == null || key.isBlank();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String key = request.getHeader(HEADER).trim();
        if (key.length() > MAX_KEY_LENGTH) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Idempotency-Key is too long");
            return;
        }
        CachedBodyRequest cachedRequest = new CachedBodyRequest(request);
        String id = request.getRequestURI() + "|" + key;
        String requestHash = sha256(cachedRequest.body());
        IdempotencyRecord existing = mongoTemplate.findById(id, IdempotencyRecord.class);
        if (existing != null) {
            replay(existing, requestHash, response);
            return;
        }
        IdempotencyRecord started = IdempotencyRecord.started(id, requestHash, clock.instant());
        try {
            mongoTemplate.insert(started);
        } catch (DuplicateKeyException race) {
            replay(mongoTemplate.findById(id, IdempotencyRecord.class), requestHash, response);
            return;
        }
        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
        try {
            chain.doFilter(cachedRequest, wrapper);
        } catch (IOException | ServletException | RuntimeException e) {
            mongoTemplate.remove(byId(id), IdempotencyRecord.class);
            throw e;
        }
        if (wrapper.getStatus() >= 500) {
            mongoTemplate.remove(byId(id), IdempotencyRecord.class);
        } else {
            mongoTemplate.save(started.completed(wrapper.getStatus(), wrapper.getContentType(), wrapper.getContentAsByteArray()));
        }
        wrapper.copyBodyToResponse();
    }

    private void replay(IdempotencyRecord existing, String requestHash, HttpServletResponse response) throws IOException {
        if (existing == null || existing.status() == IdempotencyRecord.Status.IN_PROGRESS) {
            response.sendError(HttpStatus.CONFLICT.value(), "A request with this Idempotency-Key is still in progress");
            return;
        }
        if (!existing.requestHash().equals(requestHash)) {
            response.sendError(UNPROCESSABLE_CONTENT, "Idempotency-Key was reused with a different payload");
            return;
        }
        response.setStatus(existing.responseStatus());
        response.setHeader("Idempotent-Replayed", "true");
        response.setContentType(existing.contentType() != null ? existing.contentType() : MediaType.APPLICATION_JSON_VALUE);
        if (existing.responseBody() != null) {
            StreamUtils.copy(existing.responseBody(), response.getOutputStream());
        }
    }

    private static Query byId(String id) {
        return Query.query(Criteria.where("_id").is(id));
    }

    private static String sha256(byte[] body) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(body));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
