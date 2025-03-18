package com.bytebreeze.quickdrop.repository;

import com.bytebreeze.quickdrop.model.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel, UUID> {
    // You can add custom queries here if needed
}
