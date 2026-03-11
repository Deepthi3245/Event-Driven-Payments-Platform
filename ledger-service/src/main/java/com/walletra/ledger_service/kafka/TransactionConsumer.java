package com.walletra.ledger_service.kafka;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.walletra.events.PaymentFailureEvent;
import com.walletra.events.WalletCreditedEvent;
import com.walletra.events.WalletDebitedEvent;

import com.walletra.ledger_service.services.LedgerEntryService;

@Component
public class TransactionConsumer {
	
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final LedgerEntryService ledgerEntryService;
	
	public TransactionConsumer(KafkaTemplate<String, Object> kafkaTemplate, LedgerEntryService ledgerEntryService) {
		this.kafkaTemplate = kafkaTemplate;
		this.ledgerEntryService = ledgerEntryService;
	}
	
	@KafkaListener(
			topics = "payment.failed",
			groupId = "ledger-service"
			)
	public void handlePaymentFailed(PaymentFailureEvent event) {
		ledgerEntryService.handlePaymentFailed(event);
	}
	
	@KafkaListener(
			topics = "wallet.debited",
			groupId = "ledger-service"
			)
	public void handleWalletDebited(WalletDebitedEvent event) {
		ledgerEntryService.handleWalletDebited(event);
	}
	
	@KafkaListener(
			topics = "wallet.credited.success",
			groupId = "ledger-service"
			)
	public void handleWalletCredited(WalletCreditedEvent event) {
		ledgerEntryService.handleWalletCredited(event);
	}

}
