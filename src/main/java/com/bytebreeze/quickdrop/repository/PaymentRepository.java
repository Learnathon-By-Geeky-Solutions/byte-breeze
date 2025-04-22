package com.bytebreeze.quickdrop.repository;

import com.bytebreeze.quickdrop.entity.PaymentEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {
	Optional<PaymentEntity> findByTransactionId(String transactionId);
}
