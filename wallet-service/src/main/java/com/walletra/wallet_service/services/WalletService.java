package com.walletra.wallet_service.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.walletra.events.PaymentFailureEvent;
import com.walletra.events.PaymentSuccessEvent;
import com.walletra.events.TransactionInitiatedEvent;
import com.walletra.wallet_service.enums.TransactionStatus;
import com.walletra.wallet_service.enums.TransactionType;

import com.walletra.wallet_service.kafka.WalletEventPublisher;
import com.walletra.wallet_service.models.Wallet;
import com.walletra.wallet_service.models.WalletTransaction;
import com.walletra.wallet_service.repositories.WalletRepository;
import com.walletra.wallet_service.repositories.WalletTransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class WalletService {
	
	private WalletRepository walletRepository;
	private WalletTransactionRepository walletTransactionRepository;
	private WalletEventPublisher walletEventPublisher;
	
	public WalletService(WalletRepository walletRepository, WalletTransactionRepository walletTransactionRepository, WalletEventPublisher walletEventPublisher) {
		this.walletRepository = walletRepository;
		this.walletTransactionRepository = walletTransactionRepository;
		this.walletEventPublisher = walletEventPublisher;
	}
	
	@Transactional
	public void handleDebit(TransactionInitiatedEvent event) {
		if(walletTransactionRepository.existsByTransactionIdAndType(event.transactionId(), TransactionType.DEBIT)) {
			return;
		}
		
		Wallet wallet = walletRepository.findById(event.sourceWalletId()).orElseThrow();
		
		if(wallet.getBalance().compareTo(event.amount()) < 0) {
			// Failed Transaction as amount is greater than balance
			walletTransactionRepository.save(failedDebit(event, wallet));
			
			walletEventPublisher.publishDebitFailed(event.transactionId(), event.sourceWalletId(), event.destinationWalletId(), event.amount(), event.currency(), "Balance is low than the amount to be debit");
			return;
		}
		
		wallet.setDebit(event.amount());
		wallet.setBalance(wallet.getBalance() - event.amount());
		walletRepository.save(wallet);
		// Success Transaction
		walletTransactionRepository.save(successDebit(event, wallet));
		walletEventPublisher.publishWalletDebited(event.transactionId(), event.sourceWalletId(), event.destinationWalletId(), wallet.getDebit(), event.currency());
		
		
	}
	
	public WalletTransaction failedDebit(TransactionInitiatedEvent event, Wallet wallet) {
		WalletTransaction walletTransaction = new WalletTransaction();
		walletTransaction.setAmount(event.amount());
		walletTransaction.setStatus(TransactionStatus.FAILED);
		walletTransaction.setTransactionId(event.transactionId());
		walletTransaction.setType(TransactionType.DEBIT);
		walletTransaction.setWalletId(wallet.getWalletId());
		walletTransaction.setCreatedAt(LocalDateTime.now());
		
		return walletTransaction;
	}
	
	public WalletTransaction successDebit(TransactionInitiatedEvent event, Wallet wallet) {
		WalletTransaction walletTransaction = new WalletTransaction();
		walletTransaction.setAmount(event.amount());
		walletTransaction.setStatus(TransactionStatus.SUCCESS);
		walletTransaction.setTransactionId(event.transactionId());
		walletTransaction.setType(TransactionType.DEBIT);
		walletTransaction.setWalletId(wallet.getWalletId());
		walletTransaction.setCreatedAt(LocalDateTime.now());
		
		return walletTransaction;
	}

	@Transactional
	public void handlePaymentSuccess(PaymentSuccessEvent event) {
		if(walletTransactionRepository.existsByTransactionIdAndType(event.transactionId(), TransactionType.CREDIT)) {
			return;
		}
		
		Wallet wallet = walletRepository.findById(event.destinationWalletId()).orElseThrow();
		wallet.setBalance(wallet.getBalance() + event.amount());
		wallet.setCredit(event.amount());
		wallet.setUpdatedAt(LocalDateTime.now());
		walletRepository.save(wallet);
		
		WalletTransaction walletTransaction = walletTransactionRepository.findByTransactionId(event.transactionId()).orElseThrow();
		walletTransaction.setType(TransactionType.CREDIT);
		walletTransaction.setStatus(TransactionStatus.SUCCESS);
		walletTransactionRepository.save(walletTransaction);
		
		walletEventPublisher.publishWalletCredited(event.transactionId(), event.sourceWalletId(), event.destinationWalletId(), event.amount(), event.currency());
	}

	@Transactional
	public void handlePaymentFailure(PaymentFailureEvent event) {
		
		if(walletTransactionRepository.existsByTransactionIdAndType(event.transactionId(), TransactionType.CREDIT)) {
			return;
		}
		
		Wallet wallet = walletRepository.findById(event.sourceWalletId()).orElseThrow();
		wallet.setBalance(wallet.getBalance() + event.amount());
		wallet.setCredit(event.amount());
		wallet.setUpdatedAt(LocalDateTime.now());
		walletRepository.save(wallet);
		
		WalletTransaction walletTransaction = walletTransactionRepository.findByTransactionId(event.transactionId()).orElseThrow();
		walletTransaction.setType(TransactionType.CREDIT);
		walletTransaction.setStatus(TransactionStatus.FAILED);
		walletTransactionRepository.save(walletTransaction);
		
		walletEventPublisher.publishCreditFailed(event.transactionId(), event.sourceWalletId(), event.destinationWalletId(), event.amount(), event.currency());
	}

}
