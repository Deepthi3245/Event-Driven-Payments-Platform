package com.walletra.payment_simulator_service.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.walletra.events.TransactionDebitSuccessEvent;
import com.walletra.events.WalletDebitedEvent;
import com.walletra.payment_simulator_service.enums.PaymentStatus;
import com.walletra.payment_simulator_service.kafka.PaymentEventPublisher;
import com.walletra.payment_simulator_service.models.Payment;
import com.walletra.payment_simulator_service.repositories.PaymentRepository;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {
	
	private PaymentRepository paymentRepository;
	private PaymentEventPublisher paymentEventPublisher;
	
	public PaymentService(PaymentRepository paymentRepository, PaymentEventPublisher paymentEventPublisher) {
		this.paymentRepository = paymentRepository;
		this.paymentEventPublisher = paymentEventPublisher;
	}
	

	@Transactional
	public void handlePayment(WalletDebitedEvent event) {
		
		if(paymentRepository.existsByTransactionId(event.transactionId())) {
			return;
		}
		
	    long startTime = System.currentTimeMillis();

	    Payment payment = new Payment();
	    payment.setTransactionId(event.transactionId());
	    payment.setAmount(event.amount());
	    payment.setStatus(PaymentStatus.PROCESSING);
	    payment.setCreatedAt(LocalDateTime.now());

	    try {

	        Thread.sleep(3000);

	        long endTime = System.currentTimeMillis();
	        payment.setDelayMs((double) (endTime - startTime));

	        payment.setStatus(PaymentStatus.SUCCESS);
	        payment.setUpdatedAt(LocalDateTime.now());

	        paymentRepository.save(payment);

	        paymentEventPublisher.publishPaymentSuccess(
	                event.transactionId(),
	                event.sourceWalletId(),
	                event.destinationWalletId(),
	                event.amount(),
	                event.currency()
	        );

	    } catch (InterruptedException e) {
	        long endTime = System.currentTimeMillis();
	        payment.setDelayMs((double) (endTime - startTime));

	        payment.setStatus(PaymentStatus.FAILED);
	        payment.setFailureReason("Payment Service Failed");
	        payment.setUpdatedAt(LocalDateTime.now());

	        paymentRepository.save(payment);

	        paymentEventPublisher.publishPaymentFailure(
	                event.transactionId(),
	                event.sourceWalletId(),
	                event.destinationWalletId(),
	                event.amount(),
	                event.currency(),
	                "Payment Service Failed"
	        );

	        Thread.currentThread().interrupt();
	    }
	}
}