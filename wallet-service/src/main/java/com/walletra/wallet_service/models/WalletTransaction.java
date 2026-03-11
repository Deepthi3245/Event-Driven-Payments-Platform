package com.walletra.wallet_service.models;

import java.time.LocalDateTime;
import java.util.UUID;

import com.walletra.wallet_service.enums.TransactionStatus;
import com.walletra.wallet_service.enums.TransactionType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "wallet_transactions",
uniqueConstraints = {
		@UniqueConstraint(columnNames = {"transaction_id", "type"})
})
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletTransaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID walletTransactionId;
	
	private UUID transactionId;
	
	private UUID walletId;
	
	@Enumerated(EnumType.STRING)
	private TransactionType type;
	
	private Double amount;
	
	@Enumerated(EnumType.STRING)
	private TransactionStatus status;
	
	private LocalDateTime createdAt;

}
