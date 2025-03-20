package com.bytebreeze.quickdrop.repository;

import com.bytebreeze.quickdrop.model.Parcel;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel, UUID> {
	// You can add custom queries here if needed
}
