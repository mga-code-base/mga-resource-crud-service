package com.mygym.app.service;

import com.mygym.app.models.Grievance;
import com.mygym.app.models.GrievanceWrapper;
import com.mygym.app.repository.GrievanceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        Grievance grievance = new Grievance();
        grievance.setDescription("AC not working");

        when(idGenerationService.getGrievanceId()).thenReturn(501L);
        when(repository.save(any(Grievance.class))).thenAnswer(i -> i.getArgument(0));

        Grievance result = grievanceCrudService.createGrievance(grievance);

        assertNotNull(result);
        assertEquals("501", result.getGrievanceId());
        verify(idGenerationService, times(1)).incrementGrievanceId();
    }

    @Test
    @DisplayName("Should fetch all active grievances wrapped")
    void testGetAllActiveGrievances_Success() {
        when(repository.findAllActiveGrievances()).thenReturn(List.of(new Grievance()));

        GrievanceWrapper result = grievanceCrudService.getAllActiveGrievances();

        assertNotNull(result);
        verify(repository, times(1)).findAllActiveGrievances();
    }

    @Test
    @DisplayName("Should resolve grievance and update closure date")
    void testUpdateGrienvance_Success() {
        Grievance grievance = new Grievance();
        grievance.setGrievanceId("G-501");
        grievance.setResolution("Fixed AC unit");

        Grievance existing = new Grievance();
        existing.setGrievanceId("G-501");

        when(repository.findByGrievanceId("G-501")).thenReturn(Optional.of(existing));

        Boolean updated = grievanceCrudService.updateGrienvance(grievance);

        assertTrue(updated);
        assertEquals("Fixed AC unit", existing.getResolution());
        assertNotNull(existing.getClosureDate());
        verify(repository, times(1)).save(existing);
    }
}