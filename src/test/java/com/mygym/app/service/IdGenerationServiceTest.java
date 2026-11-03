package com.mygym.app.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@ExtendWith(MockitoExtension.class)
class IdGenerationServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private IdGenerationService idGenerationService;

    @Test
    @DisplayName("Should pass init when sequence document exists in MongoDB")
    void testInit_SequenceDocumentExists_Success() {
        when(mongoTemplate.exists(any(Query.class), eq("_resources"))).thenReturn(true);

        assertDoesNotThrow(() -> idGenerationService.init());
    }

    @Test
    @DisplayName("Should fail init when sequence document is missing")
    void testInit_SequenceDocumentMissing_ThrowsException() {
        when(mongoTemplate.exists(any(Query.class), eq("_resources"))).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> idGenerationService.init());
    }

    @Test
    @DisplayName("Should reserve an ID with a single atomic increment and return the pre-increment value")
    void testNextBranchId_SingleAtomicOperation() {
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(Document.class), eq("_resources")))
                .thenReturn(new Document("nextBranchId", 105L));

        assertEquals("105", idGenerationService.nextBranchId());

        ArgumentCaptor<FindAndModifyOptions> options = ArgumentCaptor.forClass(FindAndModifyOptions.class);
        verify(mongoTemplate).findAndModify(any(Query.class), any(Update.class), options.capture(), eq(Document.class), eq("_resources"));
        assertEquals(false, options.getValue().isReturnNew());
    }

    @Test
    @DisplayName("Should fail when the sequence field cannot be incremented")
    void testNextNotificationId_MissingDocument_Throws() {
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(Document.class), eq("_resources")))
                .thenReturn(null);

        assertThrows(IllegalStateException.class, () -> idGenerationService.nextNotificationId());
    }
}
