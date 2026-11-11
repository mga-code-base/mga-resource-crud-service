package com.mygym.app.controller;

import com.mygym.app.models.Branch;
import com.mygym.app.models.BranchNameWrapper;
import com.mygym.app.models.BranchWrapper;
import com.mygym.app.models.Equipment;
import com.mygym.app.service.BranchCrudService;
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
class BranchCrudControllerTest {

    @Mock
    private BranchCrudService service;

    @InjectMocks
    private BranchCrudController controller;

    @Test
    @DisplayName("Should create branch and return generated branch ID")
    void testCreate_Success() {
        Branch branch = new Branch(null, "B-101", null, null, null, null, null, null, null);

        when(service.createBranch(any(Branch.class))).thenReturn(branch);

        String branchId = controller.create(branch);

        assertEquals("B-101", branchId);
        verify(service, times(1)).createBranch(branch);
    }

    @Test
    @DisplayName("Should fetch all branches wrapper")
    void testFetchAll_Success() {
        BranchWrapper wrapper = new BranchWrapper(List.of(new Branch(null, null, null, null, null, null, null, null, null)));
        when(service.getAllBranches()).thenReturn(wrapper);

        BranchWrapper result = controller.fetchAll();

        assertNotNull(result);
        verify(service, times(1)).getAllBranches();
    }

    @Test
    @DisplayName("Should fetch all branch names wrapper")
    void testGetAllBrancheNames_Success() {
        BranchNameWrapper wrapper = new BranchNameWrapper(null);
        when(service.getAllBranchNames()).thenReturn(wrapper);

        BranchNameWrapper result = controller.getAllBrancheNames();

        assertNotNull(result);
        verify(service, times(1)).getAllBranchNames();
    }

    @Test
    @DisplayName("Should read branch by ID")
    void testReadById_Success() {
        Branch branch = new Branch(null, "B-101", null, null, null, null, null, null, null);

        when(service.getBranchById("B-101")).thenReturn(branch);

        Branch result = controller.readById("B-101");

        assertNotNull(result);
        assertEquals("B-101", result.branchId());
    }

    @Test
    @DisplayName("Should delete branch by ID and return success message")
    void testDelete_Success() {
        doNothing().when(service).deleteBranch("B-101");

        String result = controller.delete("B-101");

        assertEquals("Branch deleted successfully with id: B-101", result);
        verify(service, times(1)).deleteBranch("B-101");
    }

    @Test
    @DisplayName("Should add equipments to branch")
    void testAddEquipmentsInBranch_Success() {
        Branch branch = new Branch(null, null, null, null, null, null, null, null, null);
        Equipment equipment = new Equipment(null, null, null, null, null);

        when(service.addEquipmentsInBranch("B-101", equipment)).thenReturn(branch);

        Branch result = controller.addEquipmentsInBranch("B-101", equipment);

        assertNotNull(result);
        verify(service, times(1)).addEquipmentsInBranch("B-101", equipment);
    }
}