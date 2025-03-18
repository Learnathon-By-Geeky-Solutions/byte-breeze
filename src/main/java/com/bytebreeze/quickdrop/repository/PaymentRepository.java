package com.bytebreeze.quickdrop.repository;

import com.bytebreeze.quickdrop.model.Payment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
  Optional<Payment> findByTransactionId(String transactionId);
}
