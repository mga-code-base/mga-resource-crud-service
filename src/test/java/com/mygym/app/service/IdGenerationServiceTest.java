package com.mygym.app.service;

import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdGenerationServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private IdGenerationService idGenerationService;

    @Test
    @DisplayName("Should pass init when resource sequence document exists")
    void testInit_Success() {
        when(mongoTemplate.exists(any(Query.class), eq("_resources"))).thenReturn(true);

        assertDoesNotThrow(() -> idGenerationService.init());
    }

    @Test
    @DisplayName("Should throw RuntimeException during init when resource sequence document is missing")
    void testInit_NotFound_ThrowsException() {
        when(mongoTemplate.exists(any(Query.class), eq("_resources"))).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> idGenerationService.init());
        assertTrue(exception.getMessage().contains("Failed to verify Resource ID sequences in MongoDB"));
    }

    @Test
    @DisplayName("Should fetch current branch ID sequence value")
    void testGetBranchId_Success() {
        Document doc = new Document("nextBranchId", 15L);
        when(mongoTemplate.findOne(any(Query.class), eq(Document.class), eq("_resources"))).thenReturn(doc);

        Long id = idGenerationService.getBranchId();

        assertEquals(15L, id);
    }

    @Test
    @DisplayName("Should increment branch ID sequence")
    void testIncrementBranchId_Success() {
        Document updatedDoc = new Document("nextBranchId", 16L);
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(Document.class), eq("_resources")))
                .thenReturn(updatedDoc);

        assertDoesNotThrow(() -> idGenerationService.incrementBranchId());
        verify(mongoTemplate, times(1)).findAndModify(any(), any(), any(), eq(Document.class), eq("_resources"));
    }
}