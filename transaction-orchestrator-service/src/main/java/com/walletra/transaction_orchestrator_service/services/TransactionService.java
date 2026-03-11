package com.walletra.transaction_orchestrator_service.services;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;

import com.walletra.events.PaymentFailureEvent;
import com.walletra.events.PaymentSuccessEvent;
import com.walletra.events.TransactionInitiatedEvent;
import com.walletra.events.TransactionRequest;
import com.walletra.events.WalletCreditedEvent;
import com.walletra.events.WalletCreditedFailedEvent;
import com.walletra.events.WalletDebitedEvent;
import com.walletra.events.WalletDebitedFailedEvent;
import com.walletra.transaction_orchestrator_service.enums.Status;
import com.walletra.transaction_orchestrator_service.kafka.TransactionEventPublisher;
import com.walletra.transaction_orchestrator_service.models.Transaction;
import com.walletra.transaction_orchestrator_service.repositories.TransactionRepository;

import jakarta.transaction.Transactional;


@Service
public class TransactionService {
	
	private TransactionRepository transactionRepository;
	private TransactionEventPublisher eventPublisher;
	
	public TransactionService(TransactionRepository transactionRepository, TransactionEventPublisher eventPublisher) {
		this.transactionRepository = transactionRepository;
		this.eventPublisher = eventPublisher;
	}

	public String createTransaction(TransactionRequest transactionRequest) {
		Transaction transaction = new Transaction();
		transaction.setAmount(transactionRequest.amount());
		transaction.setSourceWalletId(transactionRequest.sourceWalletId());
		transaction.setDestinationWalletId(transactionRequest.destinationWalletId());
		transaction.setCurrency(transactionRequest.currency());
		transaction.setCreatedAt(LocalDateTime.now());
		transaction.setUpdatedAt(LocalDateTime.now());
		transaction.setStatus(Status.INITIATED);
		transactionRepository.save(transaction);
		
		TransactionInitiatedEvent event = new TransactionInitiatedEvent(
				transaction.getTransactionId(), transaction.getSourceWalletId(),
				transaction.getDestinationWalletId(),
				transaction.getAmount(),
				transaction.getCurrency(),
				LocalDateTime.now());
		
		eventPublisher.publishTransactionInitiated(event);
		
		return "Transaction Initiated";
	
	}

	@Transactional
	public void handleTransactionSuccess(WalletCreditedEvent event) {
		
		if(transactionRepository.existsByTransactionIdAndStatus(event.transactionId(), Status.COMPLETED)) {
			return;
		}
		Transaction transaction = transactionRepository.findById(event.transactionId()).orElseThrow();
		transaction.setStatus(Status.COMPLETED);
		transaction.setUpdatedAt(LocalDateTime.now());
		transactionRepository.save(transaction);
		
	}
	
	public void handleTransactionFailed(WalletCreditedFailedEvent event) {
		
		if(transactionRepository.existsByTransactionIdAndStatus(event.transactionId(), Status.FAILED)) {
			return;
		}
		Transaction transaction = transactionRepository.findById(event.transactionId()).orElseThrow();
		transaction.setStatus(Status.FAILED);
		transaction.setUpdatedAt(LocalDateTime.now());
		transactionRepository.save(transaction);
		
	}

	public void handlePaymentSuccess(PaymentSuccessEvent event) {
		
		if(transactionRepository.existsByTransactionIdAndStatus(event.transactionId(), Status.PAYMENT_SUCCESS)) {
			return;
		}
		Transaction transaction = transactionRepository.findById(event.transactionId()).orElseThrow();
		transaction.setStatus(Status.PAYMENT_SUCCESS);
		transaction.setUpdatedAt(LocalDateTime.now());
		transactionRepository.save(transaction);
		
		eventPublisher.publishTransactionPaymentSuccess(event);
	}
	
	public void handlePaymentFailed(PaymentFailureEvent event) {
		
		if(transactionRepository.existsByTransactionIdAndStatus(event.transactionId(), Status.PAYMENT_FAILED)) {
			return;
		}
		Transaction transaction = transactionRepository.findById(event.transactionId()).orElseThrow();
		transaction.setStatus(Status.PAYMENT_FAILED);
		transaction.setUpdatedAt(LocalDateTime.now());
		transactionRepository.save(transaction);
		
		eventPublisher.publishTransactionPaymentFailed(event);
	}

	public void handleWalletDebited(WalletDebitedEvent event) {
		
		if(transactionRepository.existsByTransactionIdAndStatus(event.transactionId(), Status.DEBIT_SUCCESS)) {
			return;
		}
		Transaction transaction = transactionRepository.findById(event.transactionId()).orElseThrow();
		transaction.setStatus(Status.DEBIT_SUCCESS);
		transaction.setUpdatedAt(LocalDateTime.now());
		transactionRepository.save(transaction);
		
		eventPublisher.publishTransactionDebitSuccess(event);
	}

	public void handleWalletDebitedFailed(WalletDebitedFailedEvent event) {
		if(transactionRepository.existsByTransactionIdAndStatus(event.transactionId(), Status.DEBIT_FAILED)) {
			return;
		}
		Transaction transaction = transactionRepository.findById(event.transactionId()).orElseThrow();
		transaction.setStatus(Status.DEBIT_FAILED);
		transaction.setUpdatedAt(LocalDateTime.now());
		transactionRepository.save(transaction);
	}
	
	

}
