package com.walletra.wallet_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.walletra.events.TransactionInitiatedEvent;
import com.walletra.wallet_service.services.WalletService;

@Component
public class TransactionInitiatedConsumer {
	
	private WalletService walletService;
	
	public TransactionInitiatedConsumer(WalletService walletService) {
		this.walletService = walletService;
	}
	
	@KafkaListener(
			topics = "transaction.initiated",
			groupId = "wallet-service"
			)
	public void consume(TransactionInitiatedEvent event) {
		walletService.handleDebit(event);
	}

}
