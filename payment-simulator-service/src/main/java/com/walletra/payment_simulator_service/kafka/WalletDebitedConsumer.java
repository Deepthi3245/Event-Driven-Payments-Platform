package com.walletra.payment_simulator_service.kafka;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.walletra.events.TransactionDebitSuccessEvent;
import com.walletra.events.WalletDebitedEvent;
import com.walletra.payment_simulator_service.services.PaymentService;

@Component
public class WalletDebitedConsumer {
	
	public PaymentService paymentService;
	
	private final KafkaTemplate<String, Object> kafkaTemplate;
	
	public WalletDebitedConsumer(PaymentService paymentService, KafkaTemplate<String, Object> kafkaTemplate) {
		this.paymentService = paymentService;
		this.kafkaTemplate = kafkaTemplate;
	}
	
	@KafkaListener(
			topics = "transaction.debit.success",
			groupId = "payment-simulator-service"
			)
	public void walletDebitedConsume(WalletDebitedEvent event) {
		paymentService.handlePayment(event);
	}

}
