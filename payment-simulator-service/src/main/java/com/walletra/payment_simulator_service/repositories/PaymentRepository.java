package com.walletra.payment_simulator_service.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.walletra.payment_simulator_service.models.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

	boolean existsByTransactionId(UUID transactionId);
}
