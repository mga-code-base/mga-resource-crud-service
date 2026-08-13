package com.mygym.app.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.mygym.app.models.Branch;

@Repository
public interface BranchRepository extends MongoRepository<Branch, String> {
	public Optional<Branch> findByBranchId(String branchId);

	void deleteByBranchId(String branchId);
	
    @Query("{ 'adminIds': ?0 }")
    public Optional<Branch> findByAdminId(String adminId);
}
