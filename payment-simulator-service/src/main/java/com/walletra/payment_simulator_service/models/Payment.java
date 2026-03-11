package com.walletra.payment_simulator_service.models;

import java.time.LocalDateTime;
import java.util.UUID;

import com.walletra.payment_simulator_service.enums.PaymentStatus;

import jakarta.persistence.Column;
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

@Entity
@Table(name = "payments", uniqueConstraints = @UniqueConstraint(columnNames = "transaction_id"))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID paymentId;
	
	@Column(nullable = false, unique = true)
	private UUID transactionId;
	
	private Double amount;
	
	@Enumerated(EnumType.STRING)
	private PaymentStatus status;
	
	private String failureReason;
	
	private Double delayMs;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
}
