package com.walletra.transaction_orchestrator_service.kafka;

import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.walletra.events.PaymentFailureEvent;
import com.walletra.events.PaymentSuccessEvent;
import com.walletra.events.TransactionInitiatedEvent;
import com.walletra.events.WalletDebitedEvent;


@Component
public class TransactionEventPublisher {
	
	private final KafkaTemplate<String, Object> kafkaTemplate;
	
	public TransactionEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	public void publishTransactionInitiated(TransactionInitiatedEvent event) {
		kafkaTemplate.send("transaction.initiated",
				event.transactionId().toString(),
				event);
	}
	
	public void publishTransactionDebitSuccess(WalletDebitedEvent event) {
		kafkaTemplate.send("transaction.debit.success", event.transactionId().toString(), event);
	}

	public void publishTransactionPaymentSuccess(PaymentSuccessEvent event) {
		kafkaTemplate.send("transaction.payment.success", event.transactionId().toString(), event);
	}

	public void publishTransactionPaymentFailed(PaymentFailureEvent event) {
		kafkaTemplate.send("transaction.payment.failed", event.transactionId().toString(), event);
		
	}
		
}
