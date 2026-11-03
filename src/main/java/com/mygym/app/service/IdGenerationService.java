package com.mygym.app.service;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class IdGenerationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdGenerationService.class);
    private static final String COLLECTION_NAME = "_resources";
    private static final String TARGET_ID = "1";

    private final MongoTemplate mongoTemplate;

    public IdGenerationService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void init() {
        if (!mongoTemplate.exists(sequenceQuery(), COLLECTION_NAME)) {
            throw new IllegalStateException("Sequence document with id '" + TARGET_ID + "' was not found in '" + COLLECTION_NAME + "'");
        }
        LOGGER.info("ID generation initialised against sequence document {}", TARGET_ID);
    }

    public String nextBranchId() {
        return next("nextBranchId");
    }

    public String nextGrievanceId() {
        return next("nextGrievanceId");
    }

    public String nextNotificationId() {
        return next("nextNotificationId");
    }

    private String next(String fieldName) {
        Document previous = mongoTemplate.findAndModify(
                sequenceQuery(),
                new Update().inc(fieldName, 1),
                FindAndModifyOptions.options().returnNew(false),
                Document.class,
                COLLECTION_NAME);
        if (previous == null || previous.get(fieldName) == null) {
            throw new IllegalStateException("Failed to increment sequence field " + fieldName);
        }
        return String.valueOf(((Number) previous.get(fieldName)).longValue());
    }

    private Query sequenceQuery() {
        return new Query(Criteria.where("id").is(TARGET_ID));
    }
}
