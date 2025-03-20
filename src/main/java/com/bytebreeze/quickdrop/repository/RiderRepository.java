package com.bytebreeze.quickdrop.repository;

import com.bytebreeze.quickdrop.enums.VerificationStatus;
import com.bytebreeze.quickdrop.model.Rider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiderRepository extends JpaRepository<Rider, UUID> {
	Optional<Rider> findByEmail(String email);

	Optional<Rider> findById(UUID id);

	Optional<Rider> findByNationalIdNumber(String nationalIdNumber);

	List<Rider> findByVerificationStatus(VerificationStatus verificationStatus);
}
