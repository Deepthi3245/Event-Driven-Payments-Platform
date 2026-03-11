package com.walletra.wallet_service.models;

import java.time.LocalDateTime;
import java.util.UUID;

import com.walletra.wallet_service.enums.Status;

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
@Table(name = "wallets")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID walletId;
	
	private Double balance;
	
	private Double debit;
	
	private Double credit;
	
	private String currency;
	
	@Enumerated(EnumType.STRING)
	private Status status;
	
	private Float version;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
}
