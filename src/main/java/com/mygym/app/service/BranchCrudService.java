package com.mygym.app.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mygym.app.models.Branch;
import com.mygym.app.models.BranchNameWrapper;
import com.mygym.app.models.BranchWrapper;
import com.mygym.app.models.Equipment;
import com.mygym.app.repository.BranchRepository;

/**
 * Service managing gym branch registrations, equipment inventories, and administrative assignments.
 */
@Service
public class BranchCrudService {

    private final BranchRepository repository;
    private final IdGenerationService service;
    
    public BranchCrudService(BranchRepository repository, IdGenerationService service) {
        this.service = service;
        this.repository = repository;
    }

    public Branch createBranch(Branch branch) {
        List<Equipment> equipments = branch.getEquipments();
        AtomicInteger equipmentId = new AtomicInteger(0);
        if (Objects.nonNull(equipments)) {
            equipments.forEach(equipment -> 
                equipment.setEquipmentId(equipmentId.getAndIncrement()));
            branch.setEquipments(equipments);
        }
        branch.setBranchId(String.valueOf(service.getBranchId()));
        branch.setAdminIds(Arrays.asList(String.valueOf(service.getBranchId())));
        service.incrementBranchId();
        return repository.save(branch);
    }

    public BranchWrapper getAllBranches() {
        return new BranchWrapper(repository.findAll());
    }
    
    public BranchNameWrapper getAllBranchNames() {
        List<Branch> branches = repository.findAll();
        if (Objects.nonNull(branches) && !branches.isEmpty()) {
            return new BranchNameWrapper(
                    branches.stream()
                        .collect(Collectors.toMap(Branch::getBranchId, Branch::getName)));
        }
        return new BranchNameWrapper();
    }

    public Branch getBranchById(String id) {
        return repository.findByBranchId(id)
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + id));
    }

    public Branch updateBranch(String id, Branch updatedData) {
        Branch existing = getBranchById(id);
        existing.setName(updatedData.getName());
        existing.setPincode(updatedData.getPincode());
        return repository.save(existing);
    }
    
    public Branch addEquipmentsInBranch(String branchId, Equipment equipment) {
        Branch branch = repository.findByBranchId(branchId).orElse(null);
        if (Objects.nonNull(branch) && Objects.nonNull(branch.getEquipments())) {
            List<Equipment> equipmentList = branch.getEquipments();
            if (equipmentList.stream().noneMatch(equip -> equip.getName().matches(equipment.getName()))) {
                equipment.setEquipmentId(equipmentList.size());
                equipmentList.add(equipment);
                branch.setEquipments(equipmentList);
                repository.save(branch);
                return branch;
            }
            return new Branch("-1");
        }
        return new Branch("-1");
    }

    public void deleteBranch(String id) {
        repository.deleteByBranchId(id);
    }
    
    public Branch getBranchByAdminId(String adminId) {
        return repository.findByAdminId(adminId).orElse(new Branch());
    }

    public Branch updateEquipmentsInBranch(String branchId, Equipment equipment) {
        Branch branch = repository.findByBranchId(branchId).orElse(null);
        if (Objects.nonNull(branch) && Objects.nonNull(branch.getEquipments())
                && equipment.getEquipmentId() < branch.getEquipments().size()) {
            List<Equipment> equipmentList = branch.getEquipments();
            equipmentList.set(equipment.getEquipmentId(), equipment);
            branch.setEquipments(equipmentList);
            repository.save(branch);
            return branch;
        }
        return new Branch("-1");
    }

    public Boolean deleteByAdminId(String adminid) {
        repository.findByBranchId(adminid).ifPresent(repository::delete);
        return true;
    }

    public Boolean approveBranch(String id) {
        repository.findByBranchId(id).ifPresent(branch -> {
            branch.setIsActive(true);
            repository.save(branch);
        });
        return true;
    }
}