package com.mygym.app.repository;

import com.mygym.app.models.Grievance;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GrievanceRepository extends MongoRepository<Grievance, String> {
	
	Optional<Grievance> findByGrievanceId(String grievanceId);

	@Query("{ 'resolution': null, 'closureDate': null }")
	List<Grievance> findAllActiveGrievances();
	
	@Query("{ 'branchId': ?0, 'resolution': null, 'closureDate': null }")
	List<Grievance> findAllActiveGrievancesInBranch(String branchId);
	
	@Query("{ 'memberId': ?0, 'resolution': { $eq: null }, 'closureDate': { $eq: null } }")
	List<Grievance> findAllActiveGrievancesForMember(String memberId);
	
	@Query("{ 'memberId': ?0, 'resolution': { $ne: null }, 'closureDate': { $ne: null } }")
	List<Grievance> findAllInactiveGrievancesForMember(String memberId);
}
