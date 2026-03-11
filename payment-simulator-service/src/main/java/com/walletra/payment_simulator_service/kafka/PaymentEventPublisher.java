package com.walletra.payment_simulator_service.kafka;

import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.walletra.events.PaymentFailureEvent;
import com.walletra.events.PaymentSuccessEvent;

@Component
public class PaymentEventPublisher {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	
	public PaymentEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	public void publishPaymentSuccess(UUID transactionId, UUID sourceWalletId, UUID destinationWalletId, Double amount, String currency) {
		kafkaTemplate.send("payment.success", 
				transactionId.toString(), 
				new PaymentSuccessEvent(transactionId, sourceWalletId, destinationWalletId, amount, currency));
	}
	
	public void publishPaymentFailure(UUID transactionId, UUID sourceWalletId, UUID destinationWalletId, Double amount, String currency, String reason) {
		kafkaTemplate.send("payment.failed", 
				transactionId.toString(), 
				new PaymentFailureEvent(transactionId, sourceWalletId, destinationWalletId, amount, currency, reason));
	}
}
