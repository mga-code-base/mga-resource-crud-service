package com.mygym.app.service;

import static com.mygym.app.util.ResourceCrudConstants.DATE_FORMAT;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mygym.app.models.Grievance;
import com.mygym.app.models.GrievanceWrapper;
import com.mygym.app.repository.GrievanceRepository;
import com.mygym.app.web.NotFoundException;

@Service
public class GrievanceCrudService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    private final IdGenerationService idGenerationService;
    private final GrievanceRepository repository;

    public GrievanceCrudService(GrievanceRepository repository, IdGenerationService idGenerationService) {
        this.idGenerationService = idGenerationService;
        this.repository = repository;
    }

    public Grievance createGrievance(Grievance grievance) {
        return repository.save(grievance.withGrievanceId(idGenerationService.nextGrievanceId()));
    }

    public List<Grievance> getAllGrievances() {
        return repository.findAll();
    }

    public GrievanceWrapper getAllActiveGrievances() {
        return new GrievanceWrapper(repository.findAllActiveGrievances());
    }

    public GrievanceWrapper getAllActiveGrievancesInBranch(String branchId) {
        return new GrievanceWrapper(repository.findAllActiveGrievancesInBranch(branchId));
    }

    public Grievance getGrievanceById(String id) {
        return repository.findByGrievanceId(id)
                .orElseThrow(() -> new NotFoundException("Grievance not found with id: " + id));
    }

    public Grievance updateGrievance(String id, Grievance updatedData) {
        Grievance existing = getGrievanceById(id);
        return repository.save(new Grievance(existing._id(), existing.grievanceId(), updatedData.branchId(),
                existing.memberId(), existing.title(), updatedData.date(), updatedData.issueType(),
                updatedData.description(), updatedData.resolution(), updatedData.closureDate()));
    }

    public void deleteGrievance(String id) {
        repository.delete(getGrievanceById(id));
    }

    public GrievanceWrapper getAllActiveGrievancesForMember(String memberId) {
        return new GrievanceWrapper(repository.findAllActiveGrievancesForMember(memberId));
    }

    public GrievanceWrapper getAllInactiveGrievancesForMember(String memberId) {
        return new GrievanceWrapper(repository.findAllInactiveGrievancesForMember(memberId));
    }

    public Boolean updateGrienvance(Grievance grievance) {
        return repository.findByGrievanceId(grievance.grievanceId())
                .map(existing -> {
                    repository.save(existing.withResolution(grievance.resolution(), LocalDate.now().format(FORMATTER)));
                    return true;
                })
                .orElse(false);
    }
}
