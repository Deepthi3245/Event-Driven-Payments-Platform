package com.walletra.transaction_orchestrator_service.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.walletra.transaction_orchestrator_service.enums.Status;
import com.walletra.transaction_orchestrator_service.models.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID>{

	boolean existsByTransactionIdAndStatus(UUID transactionId, Status status);
}
