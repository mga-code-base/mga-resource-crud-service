package com.mygym.app.service;

import jakarta.annotation.PostConstruct;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * Service managing atomic sequence counter updates in MongoDB for Branch, Grievance, and Notification IDs.
 */
@Service
public class IdGenerationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    private final String COLLECTION_NAME = "_resources";
    private final String TARGET_ID = "1";

    @PostConstruct
    public void init() {
        try {
            Query query = new Query(Criteria.where("id").is(TARGET_ID));
            boolean exists = mongoTemplate.exists(query, COLLECTION_NAME);
            
            if (!exists) {
                throw new RuntimeException("Startup Error: The manual sequence document with id '" + TARGET_ID + "' was not found in '" + COLLECTION_NAME + "'.");
            }
            System.out.println("Resource ID Generation Service initialized successfully against MongoDB.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify Resource ID sequences in MongoDB", e);
        }
    }

    private Long incrementAndGet(String fieldName) {
        Query query = new Query(Criteria.where("id").is(TARGET_ID));
        Update update = new Update().inc(fieldName, 1);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
        Document updatedDoc = mongoTemplate.findAndModify(query, update, options, Document.class, COLLECTION_NAME);
        if (updatedDoc == null) {
            throw new RuntimeException("Failed to increment sequence field: " + fieldName + ". Verify document with id '" + TARGET_ID + "' exists.");
        }
        return ((Number) updatedDoc.get(fieldName)).longValue();
    }

    private Long currentValue(String fieldName) {
        Query query = new Query(Criteria.where("id").is(TARGET_ID));
        Document doc = mongoTemplate.findOne(query, Document.class, COLLECTION_NAME);
        
        if (doc == null) {
            throw new RuntimeException("Could not fetch sequence value for field: " + fieldName + " with id: " + TARGET_ID);
        }
        return ((Number) doc.get(fieldName)).longValue();
    }

    public Long getBranchId() {
        return currentValue("nextBranchId");
    }    

    public void incrementBranchId() {
        incrementAndGet("nextBranchId");
    }

    public Long getGrievanceId() {
        return currentValue("nextGrievanceId");
    }    

    public void incrementGrievanceId() {
        incrementAndGet("nextGrievanceId");
    }

    public Long getNotificationId() {
        return currentValue("nextNotificationId");
    }    

    public void incrementNotificationId() {
        incrementAndGet("nextNotificationId");
    }
}