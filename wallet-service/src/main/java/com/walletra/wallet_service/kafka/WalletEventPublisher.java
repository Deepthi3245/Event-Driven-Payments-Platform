package com.walletra.wallet_service.kafka;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.walletra.events.WalletCreditedEvent;
import com.walletra.events.WalletCreditedFailedEvent;
import com.walletra.events.WalletDebitedEvent;
import com.walletra.events.WalletDebitedFailedEvent;


@Component
public class WalletEventPublisher {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public WalletEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void publishWalletDebited(UUID transactionId, UUID sourceWalletId, UUID destinationWalletId, Double amount, String currency) {
		kafkaTemplate.send("wallet.debited", transactionId.toString(), new WalletDebitedEvent(transactionId, sourceWalletId, destinationWalletId,
				amount, currency, "Wallet Debited", LocalDateTime.now()));
	}

	public void publishDebitFailed(UUID transactionId, UUID sourceWalletId, UUID destinationWalletId, Double amount, String currency, String reason) {
		kafkaTemplate.send("wallet.debited.failed", transactionId.toString(),
				new WalletDebitedFailedEvent(transactionId, sourceWalletId, destinationWalletId, amount,
						currency, reason, LocalDateTime.now()));
	}
	
	public void publishWalletCredited(UUID transactionId, UUID sourceWalletId, UUID destinationWalletId, Double amount, String currency) {
		kafkaTemplate.send("wallet.credited.success", transactionId.toString(), new WalletCreditedEvent(transactionId, sourceWalletId, destinationWalletId,
				amount, currency, "Wallet Credited", LocalDateTime.now()));
	}
	
	public void publishCreditFailed(UUID transactionId, UUID sourceWalletId, UUID destinationWalletId, Double amount, String currency) {
		kafkaTemplate.send("wallet.credited.failed", transactionId.toString(), new WalletCreditedFailedEvent(transactionId, sourceWalletId, destinationWalletId,
				amount, currency, "Wallet Credit Failed", LocalDateTime.now()));
	}

}
