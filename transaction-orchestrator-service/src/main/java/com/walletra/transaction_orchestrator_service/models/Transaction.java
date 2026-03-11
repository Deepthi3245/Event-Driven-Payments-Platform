package com.walletra.transaction_orchestrator_service.models;

import java.time.LocalDateTime;
import java.util.UUID;

import com.walletra.transaction_orchestrator_service.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID transactionId;
	
	private UUID sourceWalletId;
	
	private UUID destinationWalletId;
	
	private Double amount;
	
	private String currency;
	
	@Enumerated(EnumType.STRING)
	private Status status;
	
	@Column(nullable = true)
	private String failureReason;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;

}
