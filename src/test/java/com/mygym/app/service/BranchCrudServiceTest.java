package com.mygym.app.service;

import com.mygym.app.models.Branch;
import com.mygym.app.models.BranchWrapper;
import com.mygym.app.models.Equipment;
import com.mygym.app.repository.BranchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchCrudServiceTest {

    @Mock
    private BranchRepository repository;

    @Mock
    private IdGenerationService idGenerationService;

    @InjectMocks
    private BranchCrudService branchCrudService;

    @Test
    @DisplayName("Should create branch and increment ID sequence")
    void testCreateBranch_Success() {
        Branch branch = new Branch(null, null, "Downtown Gym", null, null, null, null, new ArrayList<>(), null);

        when(idGenerationService.nextBranchId()).thenReturn("10");
        when(repository.save(any(Branch.class))).thenAnswer(i -> i.getArgument(0));

        Branch result = branchCrudService.createBranch(branch);

        assertNotNull(result);
        assertEquals("10", result.branchId());
    }

    @Test
    @DisplayName("Should fetch all branches wrapped")
    void testGetAllBranches_Success() {
        when(repository.findAll()).thenReturn(List.of(new Branch(null, null, null, null, null, null, null, null, null)));

        BranchWrapper result = branchCrudService.getAllBranches();

        assertNotNull(result);
        assertEquals(1, result.allBranches().size());
    }

    @Test
    @DisplayName("Should approve branch by updating isActive status")
    void testApproveBranch_Success() {
        Branch branch = new Branch(null, "B-101", null, null, null, null, null, null, false);

        when(repository.findByBranchId("B-101")).thenReturn(Optional.of(branch));

        Boolean approved = branchCrudService.approveBranch("B-101");

        assertTrue(approved);
        ArgumentCaptor<Branch> saved = ArgumentCaptor.forClass(Branch.class);
        verify(repository, times(1)).save(saved.capture());
        assertTrue(saved.getValue().isActive());
    }
}