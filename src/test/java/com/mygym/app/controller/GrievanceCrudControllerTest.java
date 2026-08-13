package com.mygym.app.controller;

import com.mygym.app.models.Grievance;
import com.mygym.app.models.GrievanceWrapper;
import com.mygym.app.service.GrievanceCrudService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrievanceCrudControllerTest {

    @Mock
    private GrievanceCrudService service;

    @InjectMocks
    private GrievanceCrudController controller;

    @Test
    @DisplayName("Should fetch all active grievances wrapper")
    void testGetAllGrievances_Success() {
        GrievanceWrapper wrapper = new GrievanceWrapper(List.of(new Grievance()));
        when(service.getAllActiveGrievances()).thenReturn(wrapper);

        GrievanceWrapper result = controller.getAllGrievances();

        assertNotNull(result);
        verify(service, times(1)).getAllActiveGrievances();
    }

    @Test
    @DisplayName("Should create grievance and return ID")
    void testCreate_Success() {
        Grievance grievance = new Grievance();
        grievance.setGrievanceId("G-501");

        when(service.createGrievance(any(Grievance.class))).thenReturn(grievance);

        String grievanceId = controller.create(grievance);

        assertEquals("G-501", grievanceId);
    }

    @Test
    @DisplayName("Should read grievance by ID")
    void testReadById_Success() {
        Grievance grievance = new Grievance();
        grievance.setGrievanceId("G-501");

        when(service.getGrievanceById("G-501")).thenReturn(grievance);

        Grievance result = controller.readById("G-501");

        assertNotNull(result);
        assertEquals("G-501", result.getGrievanceId());
    }

    @Test
    @DisplayName("Should delete grievance by ID")
    void testDelete_Success() {
        doNothing().when(service).deleteGrievance("G-501");

        String result = controller.delete("G-501");

        assertEquals("Grievance entry deleted successfully with id: G-501", result);
        verify(service, times(1)).deleteGrievance("G-501");
    }
}