package com.bytebreeze.quickdrop.repository;

import com.bytebreeze.quickdrop.entity.RiderEntity;
import com.bytebreeze.quickdrop.enums.VerificationStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiderRepository extends JpaRepository<RiderEntity, UUID> {
	Optional<RiderEntity> findByEmail(String email);

	Optional<RiderEntity> findById(UUID id);

	Optional<RiderEntity> findByNationalIdNumber(String nationalIdNumber);

	List<RiderEntity> findByVerificationStatus(VerificationStatus verificationStatus);
}
