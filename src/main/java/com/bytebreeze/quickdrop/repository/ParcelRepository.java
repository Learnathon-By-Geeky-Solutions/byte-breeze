package com.bytebreeze.quickdrop.repository;

import com.bytebreeze.quickdrop.enums.ParcelStatus;
import com.bytebreeze.quickdrop.entity.ParcelEntity;
import com.bytebreeze.quickdrop.entity.RiderEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ParcelRepository extends JpaRepository<ParcelEntity, UUID> {
	@Query("SELECT p FROM ParcelEntity p WHERE p.sender.id = :senderId AND p.status = :status")
	List<ParcelEntity> findBySenderAndStatus(UUID senderId, ParcelStatus status);

	boolean existsByTrackingId(String trackingId);

	@Query("SELECT p FROM ParcelEntity p WHERE p.sender.id = :senderId")
	List<ParcelEntity> getAllBySender(UUID senderId);

	List<ParcelEntity> findByStatusAndRiderIsNull(ParcelStatus status);

	List<ParcelEntity> findByStatusInAndRider(List<ParcelStatus> statuses, RiderEntity rider);

	List<ParcelEntity> findByRider(RiderEntity rider);
}
