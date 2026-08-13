package com.mygym.app.service;

import static com.mygym.app.util.ResourceCrudConstants.DATE_FORMAT;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.mygym.app.models.Grievance;
import com.mygym.app.models.GrievanceWrapper;
import com.mygym.app.repository.GrievanceRepository;

/**
 * Service managing grievance feedback submissions, active resolution tracking, and closure statuses.
 */
@Service
public class GrievanceCrudService {
    
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private final IdGenerationService service;
    private final GrievanceRepository repository;

    public GrievanceCrudService(GrievanceRepository repository, IdGenerationService service) {
        this.service = service;
        this.repository = repository;
    }

    public Grievance createGrievance(Grievance grievance) {
        grievance.setGrievanceId(String.valueOf(service.getGrievanceId()));
        service.incrementGrievanceId();
        return repository.save(grievance);
    }

    public List<Grievance> getAllGrievances() {
        return repository.findAll();
    }
    
    public GrievanceWrapper getAllActiveGrievances() {
        List<Grievance> activeGrievances = repository.findAllActiveGrievances();
        return (Objects.isNull(activeGrievances) || activeGrievances.isEmpty()) 
                ? new GrievanceWrapper() 
                : new GrievanceWrapper(activeGrievances);
    }
    
    public GrievanceWrapper getAllActiveGrievancesInBranch(String branchId) {
        List<Grievance> activeGrievances = repository.findAllActiveGrievancesInBranch(branchId);
        return (Objects.nonNull(activeGrievances) && activeGrievances.isEmpty()) 
                ? new GrievanceWrapper() 
                : new GrievanceWrapper(activeGrievances);
    }

    public Grievance getGrievanceById(String id) {
        return repository.findByGrievanceId(id)
                .orElseThrow(() -> new RuntimeException("Grievance not found with id: " + id));
    }

    public Grievance updateGrievance(String id, Grievance updatedData) {
        Grievance existing = getGrievanceById(id);
        existing.setBranchId(updatedData.getBranchId());
        existing.setDate(updatedData.getDate());
        existing.setIssueType(updatedData.getIssueType());
        existing.setDescription(updatedData.getDescription());
        existing.setResolution(updatedData.getResolution());
        existing.setClosureDate(updatedData.getClosureDate());
        return repository.save(existing);
    }

    public void deleteGrievance(String id) {
        Grievance existing = getGrievanceById(id);
        repository.delete(existing);
    }

    public GrievanceWrapper getAllActiveGrievancesForMember(String memberId) {
        List<Grievance> activeGrievances = repository.findAllActiveGrievancesForMember(memberId);
        return (Objects.nonNull(activeGrievances) && activeGrievances.isEmpty()) 
                ? new GrievanceWrapper() 
                : new GrievanceWrapper(activeGrievances);
    }

    public GrievanceWrapper getAllInactiveGrievancesForMember(String memberId) {
        List<Grievance> activeGrievances = repository.findAllInactiveGrievancesForMember(memberId);
        return (Objects.nonNull(activeGrievances) && activeGrievances.isEmpty()) 
                ? new GrievanceWrapper() 
                : new GrievanceWrapper(activeGrievances);
    }

    public Boolean updateGrienvance(Grievance grievance) {
        return repository.findByGrievanceId(grievance.getGrievanceId()).map(doc -> {
            doc.setClosureDate(LocalDate.now().format(formatter));
            doc.setResolution(grievance.getResolution());
            repository.save(doc);
            return true;
        }).orElse(false);
    }
}