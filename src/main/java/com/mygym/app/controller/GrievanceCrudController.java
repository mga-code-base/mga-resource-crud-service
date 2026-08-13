package com.mygym.app.controller;

import static com.mygym.app.util.ResourceCrudConstants.ALL_ACTIVE_BY_BRACH_ID_ENDPOINT;
import static com.mygym.app.util.ResourceCrudConstants.ALL_ACTIVE_BY_MEMBER_ID_ENDPOINT;
import static com.mygym.app.util.ResourceCrudConstants.ALL_ACTIVE_ENDPOINT;
import static com.mygym.app.util.ResourceCrudConstants.ALL_INACTIVE_BY_MEMBER_ID_ENDPOINT;
import static com.mygym.app.util.ResourceCrudConstants.GRIEVANCE_CRUD_REST_BASE;
import static com.mygym.app.util.ResourceCrudConstants.ID_PLACEHOLDER_ENDPOINT;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mygym.app.models.Grievance;
import com.mygym.app.models.GrievanceWrapper;
import com.mygym.app.service.GrievanceCrudService;

@RestController
@RequestMapping(GRIEVANCE_CRUD_REST_BASE)
public class GrievanceCrudController {

    private final GrievanceCrudService service;

    public GrievanceCrudController(GrievanceCrudService service) {
        this.service = service;
    }
    
    @GetMapping(ALL_ACTIVE_ENDPOINT)
    public GrievanceWrapper getAllGrievances() {
    	return service.getAllActiveGrievances() ;
    }
    
    @GetMapping(ALL_ACTIVE_BY_BRACH_ID_ENDPOINT)
    public GrievanceWrapper getAllActiveGrievancesInBranch(@PathVariable("id") String branchId) {
    	return service.getAllActiveGrievancesInBranch(branchId) ;
    }

    @PostMapping
    public String create(@RequestBody Grievance grievance) {
        return service.createGrievance(grievance).getGrievanceId();
    }

    @GetMapping
    public List<Grievance> readAll() {
        return service.getAllGrievances();
    }

    @GetMapping(ID_PLACEHOLDER_ENDPOINT)
    public Grievance readById(@PathVariable String id) {
        return service.getGrievanceById(id);
    }

    @PutMapping(ID_PLACEHOLDER_ENDPOINT)
    public Grievance update(@PathVariable String id, @RequestBody Grievance grievance) {
        return service.updateGrievance(id, grievance);
    }

    @DeleteMapping(ID_PLACEHOLDER_ENDPOINT)
    public String delete(@PathVariable String id) {
        service.deleteGrievance(id);
        return "Grievance entry deleted successfully with id: " + id;
    }
    
    @GetMapping(ALL_ACTIVE_BY_MEMBER_ID_ENDPOINT)
    public GrievanceWrapper getAllActiveGrievancesForMember(@PathVariable("id") String memberId) {
    	return service.getAllActiveGrievancesForMember(memberId) ;
    }
    
    @GetMapping(ALL_INACTIVE_BY_MEMBER_ID_ENDPOINT)
    public GrievanceWrapper getAllInactiveGrievancesInBranch(@PathVariable("id") String memberId) {
    	return service.getAllInactiveGrievancesForMember(memberId) ;
    }
    
    @PutMapping
    public Boolean updateGrienvance(@RequestBody Grievance grievance) {
    	if(service.updateGrienvance(grievance)) 
    		return true;
    	return false;
    }
}
