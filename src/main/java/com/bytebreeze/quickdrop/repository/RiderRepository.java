package com.bytebreeze.quickdrop.repository;

import com.bytebreeze.quickdrop.model.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiderRepository extends JpaRepository<Rider, Long>{

}
