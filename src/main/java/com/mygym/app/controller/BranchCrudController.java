package com.mygym.app.controller;

import static com.mygym.app.util.ResourceCrudConstants.ALL_BRANCH_NAME_ENDPOINT;
import static com.mygym.app.util.ResourceCrudConstants.ALL_ENDPOINT;
import static com.mygym.app.util.ResourceCrudConstants.APPROVE_ID_PLACEHOLDER_ENDPOINT;
import static com.mygym.app.util.ResourceCrudConstants.BRANCHES_CRUD_REST_BASE;
import static com.mygym.app.util.ResourceCrudConstants.DELETE_BRANCH_BY_ADMINID;
import static com.mygym.app.util.ResourceCrudConstants.EQUIPMENTS_ADD;
import static com.mygym.app.util.ResourceCrudConstants.EQUIPMENTS_UPDATE;
import static com.mygym.app.util.ResourceCrudConstants.GET_BRANCH_BY_ADMINID;
import static com.mygym.app.util.ResourceCrudConstants.ID_PLACEHOLDER_ENDPOINT;

import jakarta.validation.Valid;
import jakarta.validation.groups.Default;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mygym.app.models.Branch;
import com.mygym.app.models.BranchNameWrapper;
import com.mygym.app.models.BranchWrapper;
import com.mygym.app.models.Equipment;
import com.mygym.app.service.BranchCrudService;
import com.mygym.app.validation.OnCreate;

@RestController
@RequestMapping(BRANCHES_CRUD_REST_BASE)
public class BranchCrudController {

    private final BranchCrudService service;

    public BranchCrudController(BranchCrudService service) {
        this.service = service;
    }

    @PostMapping
    public String create(@Validated({Default.class, OnCreate.class}) @RequestBody Branch branch) {
        return service.createBranch(branch).branchId();
    }

    @GetMapping(ALL_ENDPOINT)
    public BranchWrapper fetchAll() {
        return service.getAllBranches();
    }
    
    @GetMapping(ALL_BRANCH_NAME_ENDPOINT)
    public BranchNameWrapper getAllBrancheNames() {
        return service.getAllBranchNames();
    }

    @GetMapping(ID_PLACEHOLDER_ENDPOINT)
    public Branch readById(@PathVariable String id) {
        return service.getBranchById(id);
    }

    @PutMapping(ID_PLACEHOLDER_ENDPOINT)
    public Branch update(@PathVariable String id, @Valid @RequestBody Branch branch) {
        return service.updateBranch(id, branch);
    }

    @DeleteMapping(ID_PLACEHOLDER_ENDPOINT)
    public String delete(@PathVariable String id) {
        service.deleteBranch(id);
        return "Branch deleted successfully with id: " + id;
    }
    
    @PutMapping(EQUIPMENTS_ADD)
    public Branch addEquipmentsInBranch(@PathVariable(name = "id") String branchId, @Valid @RequestBody Equipment equipments) {
    	return service.addEquipmentsInBranch(branchId, equipments);
    }
    
    @PutMapping(EQUIPMENTS_UPDATE)
    public Branch updateEquipmentsInBranch(@PathVariable(name = "id") String branchId,@Valid @RequestBody Equipment equipments) {
    	return service.updateEquipmentsInBranch(branchId, equipments);
    }
    
    @GetMapping(GET_BRANCH_BY_ADMINID)
    public Branch getBranchByAdminId(@PathVariable(name = "adminid") String adminId) {
    	return service.getBranchByAdminId(adminId);
    }
    
    @DeleteMapping(DELETE_BRANCH_BY_ADMINID)
    public Boolean deleteByAdminId(@PathVariable("adminid") String adminid) {
        return service.deleteByAdminId(adminid);
    }
    
    @PutMapping(APPROVE_ID_PLACEHOLDER_ENDPOINT)
    public Boolean approveBranch(@PathVariable String id) {
        return service.approveBranch(id);
    }
}
