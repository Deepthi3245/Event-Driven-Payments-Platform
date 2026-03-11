package com.walletra.wallet_service.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.walletra.wallet_service.enums.TransactionType;
import com.walletra.wallet_service.models.WalletTransaction;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, UUID> {

	boolean existsByTransactionIdAndType(UUID transactionId, TransactionType type);
	
	Optional<WalletTransaction> findByTransactionId(UUID transactionId);
}
