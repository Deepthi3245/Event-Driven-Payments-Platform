package com.walletra.wallet_service.kafka;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.walletra.events.PaymentFailureEvent;
import com.walletra.events.PaymentSuccessEvent;
import com.walletra.wallet_service.services.WalletService;

@Component
public class PaymentConsumer {
	
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final WalletService walletService;
	
	public PaymentConsumer(KafkaTemplate kafkaTemplate, WalletService walletService) {
		this.kafkaTemplate = kafkaTemplate;
		this.walletService = walletService;
		
	}
	
	@KafkaListener(
			topics = "transaction.payment.success",
			groupId =  "wallet-service"
			)
	public void handlePaymentSuccess(PaymentSuccessEvent paymentSuccessEvent) {
		walletService.handlePaymentSuccess(paymentSuccessEvent);
	}
	
	@KafkaListener(
			topics = "transaction.payment.failed",
			groupId =  "wallet-service"
			)
	public void handlePaymentFailure(PaymentFailureEvent paymentFailureEvent) {
		walletService.handlePaymentFailure(paymentFailureEvent);
	}

}
