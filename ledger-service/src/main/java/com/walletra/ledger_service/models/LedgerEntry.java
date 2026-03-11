package com.walletra.ledger_service.models;

import java.time.LocalDateTime;
import java.util.UUID;

import com.walletra.ledger_service.enums.Direction;
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

@Table(name = "ledger_entry")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LedgerEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID ledgerEntryId;
	
	private UUID transactionId;
	
	private UUID walletId;
	
	@Enumerated(EnumType.STRING)
	private Direction direction;
	
	private Double amount;
	
	private String currency;
	
	private String eventType;
	
	private LocalDateTime createdAt;
}
