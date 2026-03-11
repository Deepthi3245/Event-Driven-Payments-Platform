package com.walletra.transaction_orchestrator_service.kafka;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.walletra.events.PaymentFailureEvent;
import com.walletra.events.PaymentSuccessEvent;
import com.walletra.events.WalletCreditedEvent;
import com.walletra.events.WalletCreditedFailedEvent;
import com.walletra.events.WalletDebitedEvent;
import com.walletra.events.WalletDebitedFailedEvent;
import com.walletra.transaction_orchestrator_service.services.TransactionService;

@Component
public class TransactionEventConsumer {
	
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final TransactionService transactionService;
	
	public TransactionEventConsumer(KafkaTemplate<String, Object> kafkaTemplate, TransactionService transactionService) {
		this.kafkaTemplate = kafkaTemplate;
		this.transactionService = transactionService;
	}
	
	@KafkaListener(
			topics = "wallet.credited.success",
			groupId = "transaction-orchestrator-service"
			)
	public void handleTransactionSuccess(WalletCreditedEvent event) {
		transactionService.handleTransactionSuccess(event);
	}
	
	@KafkaListener(
			topics = "wallet.credited.failed",
			groupId = "transaction-orchestrator-service"
			)
	public void handleTransactionFailed(WalletCreditedFailedEvent event) {
		transactionService.handleTransactionFailed(event);
	}
	
	@KafkaListener(
			topics = "payment.success",
			groupId = "transaction-orchestrator-service"
			)
	public void handlePaymentSuccess(PaymentSuccessEvent event) {
		transactionService.handlePaymentSuccess(event);
	}
	
	@KafkaListener(
			topics = "payment.failed",
			groupId = "transaction-orchestrator-service"
			)
	public void handlePaymentFailed(PaymentFailureEvent event) {
		transactionService.handlePaymentFailed(event);
	}
	
	@KafkaListener(
			topics = "wallet.debited",
			groupId = "transaction-orchestrator-service"
			)
	public void handleWalletDebited(WalletDebitedEvent event) {
		transactionService.handleWalletDebited(event);
	}
	
	@KafkaListener(
			topics = "wallet.debited.failed",
			groupId = "transaction-orchestrator-service"
			)
	public void handleWalletDebitedFailed(WalletDebitedFailedEvent event) {
		transactionService.handleWalletDebitedFailed(event);
	}

}
