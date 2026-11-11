package com.mygym.app.idempotency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class IdempotencyFilterTest {

    private static final String BODY = "{\"name\":\"Andheri West\"}";
    private final MongoTemplate mongo = mock(MongoTemplate.class);
    private final IdempotencyFilter filter = new IdempotencyFilter(mongo);

    @Test
    @DisplayName("Should run the request once and store its response under the key")
    void testFirstRequestIsStored() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
                res.setStatus(201);
                res.setContentType("application/json");
                res.getWriter().write("\"42\"");
            }
        });

        filter.doFilter(post("key-1"), response, chain);

        ArgumentCaptor<IdempotencyRecord> saved = ArgumentCaptor.forClass(IdempotencyRecord.class);
        verify(mongo).save(saved.capture());
        assertEquals(IdempotencyRecord.Status.COMPLETED, saved.getValue().status());
        assertEquals(201, saved.getValue().responseStatus());
        assertEquals(201, response.getStatus());
        assertEquals("\"42\"", response.getContentAsString());
    }

    @Test
    @DisplayName("Should replay the stored response for a retried request")
    void testRetryIsReplayed() throws Exception {
        IdempotencyRecord stored = IdempotencyRecord.started("/crud/branches|key-1", sha256(BODY), Instant.now())
                .completed(201, "application/json", "\"42\"".getBytes(StandardCharsets.UTF_8));
        when(mongo.findById(eq("/crud/branches|key-1"), eq(IdempotencyRecord.class))).thenReturn(stored);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(post("key-1"), response, chain);

        assertEquals(201, response.getStatus());
        assertEquals("\"42\"", response.getContentAsString());
        assertEquals("true", response.getHeader("Idempotent-Replayed"));
        verify(mongo, never()).insert(any(IdempotencyRecord.class));
    }

    @Test
    @DisplayName("Should reject a reused key with a different payload")
    void testReusedKeyWithDifferentBody() throws Exception {
        IdempotencyRecord stored = IdempotencyRecord.started("/crud/branches|key-1", "other-hash", Instant.now())
                .completed(201, "application/json", new byte[0]);
        when(mongo.findById(eq("/crud/branches|key-1"), eq(IdempotencyRecord.class))).thenReturn(stored);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(post("key-1"), response, new MockFilterChain());

        assertEquals(422, response.getStatus());
    }

    @Test
    @DisplayName("Should answer 409 while the first request is still running")
    void testConcurrentDuplicate() throws Exception {
        IdempotencyRecord inFlight = IdempotencyRecord.started("/crud/branches|key-1", sha256(BODY), Instant.now());
        when(mongo.findById(eq("/crud/branches|key-1"), eq(IdempotencyRecord.class))).thenReturn(inFlight);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(post("key-1"), response, new MockFilterChain());

        assertEquals(409, response.getStatus());
    }

    private MockHttpServletRequest post(String key) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/crud/branches");
        request.addHeader(IdempotencyFilter.HEADER, key);
        request.setContentType("application/json");
        request.setContent(BODY.getBytes(StandardCharsets.UTF_8));
        return request;
    }

    private static String sha256(String body) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(body.getBytes(StandardCharsets.UTF_8)));
    }
}
