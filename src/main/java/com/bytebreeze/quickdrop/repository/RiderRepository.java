package com.bytebreeze.quickdrop.repository;

import com.bytebreeze.quickdrop.model.Rider;
import com.bytebreeze.quickdrop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RiderRepository extends JpaRepository<Rider, UUID>{
    Optional<Rider> findByEmail(String email);
    Optional <Rider> findById(UUID id);

}
