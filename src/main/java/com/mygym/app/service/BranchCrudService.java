package com.mygym.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.mygym.app.models.Branch;
import com.mygym.app.models.BranchNameWrapper;
import com.mygym.app.models.BranchWrapper;
import com.mygym.app.models.Equipment;
import com.mygym.app.repository.BranchRepository;
import com.mygym.app.web.NotFoundException;

@Service
public class BranchCrudService {

    private final BranchRepository repository;
    private final IdGenerationService idGenerationService;

    public BranchCrudService(BranchRepository repository, IdGenerationService idGenerationService) {
        this.repository = repository;
        this.idGenerationService = idGenerationService;
    }

    public Branch createBranch(Branch branch) {
        String branchId = idGenerationService.nextBranchId();
        List<Equipment> equipments = branch.equipments() == null ? null
                : IntStream.range(0, branch.equipments().size())
                        .mapToObj(index -> branch.equipments().get(index).withEquipmentId(index))
                        .collect(Collectors.toCollection(ArrayList::new));
        return repository.save(branch
                .withBranchId(branchId)
                .withAdminIds(List.of(branchId))
                .withEquipments(equipments));
    }

    public BranchWrapper getAllBranches() {
        return new BranchWrapper(repository.findAll());
    }

    public BranchNameWrapper getAllBranchNames() {
        return new BranchNameWrapper(repository.findAll().stream()
                .collect(Collectors.toMap(Branch::branchId, Branch::name)));
    }

    public Branch getBranchById(String id) {
        return repository.findByBranchId(id)
                .orElseThrow(() -> new NotFoundException("Branch not found with id: " + id));
    }

    public Branch updateBranch(String id, Branch updatedData) {
        Branch existing = getBranchById(id);
        return repository.save(new Branch(existing._id(), existing.branchId(), updatedData.name(), existing.adminIds(),
                updatedData.pincode(), existing.address(), existing.city(), existing.equipments(), existing.isActive()));
    }

    public Branch addEquipmentsInBranch(String branchId, Equipment equipment) {
        return repository.findByBranchId(branchId)
                .filter(branch -> Objects.nonNull(branch.equipments()))
                .filter(branch -> branch.equipments().stream().noneMatch(existing -> Objects.equals(existing.name(), equipment.name())))
                .map(branch -> {
                    List<Equipment> equipments = new ArrayList<>(branch.equipments());
                    equipments.add(equipment.withEquipmentId(equipments.size()));
                    return repository.save(branch.withEquipments(equipments));
                })
                .orElse(Branch.notFound());
    }

    public void deleteBranch(String id) {
        repository.deleteByBranchId(id);
    }

    public Branch getBranchByAdminId(String adminId) {
        return repository.findByAdminId(adminId)
                .orElse(new Branch(null, null, null, null, null, null, null, null, null));
    }

    public Branch updateEquipmentsInBranch(String branchId, Equipment equipment) {
        return repository.findByBranchId(branchId)
                .filter(branch -> Objects.nonNull(branch.equipments()))
                .filter(branch -> equipment.equipmentId() != null && equipment.equipmentId() < branch.equipments().size())
                .map(branch -> {
                    List<Equipment> equipments = new ArrayList<>(branch.equipments());
                    equipments.set(equipment.equipmentId(), equipment);
                    return repository.save(branch.withEquipments(equipments));
                })
                .orElse(Branch.notFound());
    }

    public Boolean deleteByAdminId(String adminId) {
        repository.findByBranchId(adminId).ifPresent(repository::delete);
        return true;
    }

    public Boolean approveBranch(String id) {
        repository.findByBranchId(id).ifPresent(branch -> repository.save(branch.withIsActive(true)));
        return true;
    }
}
