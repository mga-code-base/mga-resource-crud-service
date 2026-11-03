package com.mygym.app.service;

import com.mygym.app.models.Grievance;
import com.mygym.app.models.GrievanceWrapper;
import com.mygym.app.repository.GrievanceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrievanceCrudServiceTest {

    @Mock
    private GrievanceRepository repository;

    @Mock
    private IdGenerationService idGenerationService;

    @InjectMocks
    private GrievanceCrudService grievanceCrudService;

    @Test
    @DisplayName("Should create grievance and increment grievance ID sequence")
    void testCreateGrievance_Success() {
        Grievance grievance = new Grievance(null, null, null, null, null, null, null, "AC not working", null, null);

        when(idGenerationService.nextGrievanceId()).thenReturn("501");
        when(repository.save(any(Grievance.class))).thenAnswer(i -> i.getArgument(0));

        Grievance result = grievanceCrudService.createGrievance(grievance);

        assertNotNull(result);
        assertEquals("501", result.grievanceId());
    }

    @Test
    @DisplayName("Should fetch all active grievances wrapped")
    void testGetAllActiveGrievances_Success() {
        when(repository.findAllActiveGrievances()).thenReturn(List.of(new Grievance(null, null, null, null, null, null, null, null, null, null)));

        GrievanceWrapper result = grievanceCrudService.getAllActiveGrievances();

        assertNotNull(result);
        verify(repository, times(1)).findAllActiveGrievances();
    }

    @Test
    @DisplayName("Should resolve grievance and update closure date")
    void testUpdateGrienvance_Success() {
        Grievance grievance = new Grievance(null, "G-501", null, null, null, null, null, null, "Fixed AC unit", null);

        Grievance existing = new Grievance(null, "G-501", null, null, null, null, null, null, null, null);

        when(repository.findByGrievanceId("G-501")).thenReturn(Optional.of(existing));

        Boolean updated = grievanceCrudService.updateGrienvance(grievance);

        assertTrue(updated);
        ArgumentCaptor<Grievance> saved = ArgumentCaptor.forClass(Grievance.class);
        verify(repository, times(1)).save(saved.capture());
        assertEquals("Fixed AC unit", saved.getValue().resolution());
        assertNotNull(saved.getValue().closureDate());
    }
}