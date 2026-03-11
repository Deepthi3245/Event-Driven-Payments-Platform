package com.walletra.ledger_service.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.walletra.events.PaymentFailureEvent;
import com.walletra.events.WalletCreditedEvent;
import com.walletra.events.WalletDebitedEvent;
import com.walletra.ledger_service.enums.Direction;
import com.walletra.ledger_service.models.LedgerEntry;
import com.walletra.ledger_service.repositories.LedgerEntryRepository;

@Service
public class LedgerEntryService {
	
	private final LedgerEntryRepository ledgerEntryRepository;
	
	public LedgerEntryService(LedgerEntryRepository ledgerEntryRepository) {
		this.ledgerEntryRepository = ledgerEntryRepository;
	}

	public void handleWalletDebited(WalletDebitedEvent event) {
		
		if(ledgerEntryRepository.existsByTransactionIdAndDirectionAndEventType(event.transactionId(), Direction.DEBIT, "wallet.debited")) {
			return;
		}
		LedgerEntry ledgerEntry = new LedgerEntry();
		ledgerEntry.setTransactionId(event.transactionId());
		ledgerEntry.setWalletId(event.sourceWalletId());
		ledgerEntry.setAmount(event.amount());
		ledgerEntry.setCurrency(event.currency());
		ledgerEntry.setDirection(Direction.DEBIT);
		ledgerEntry.setCreatedAt(LocalDateTime.now());
		ledgerEntry.setEventType("wallet.debited");
		ledgerEntryRepository.save(ledgerEntry);
		
	}
	
	public void handleWalletCredited(WalletCreditedEvent event) {
		if(ledgerEntryRepository.existsByTransactionIdAndDirectionAndEventType(event.transactionId(), Direction.CREDIT, "wallet.credited.success")) {
			return;
		}
		LedgerEntry ledgerEntry = new LedgerEntry();
		ledgerEntry.setTransactionId(event.transactionId());
		ledgerEntry.setWalletId(event.destinationWalletId());
		ledgerEntry.setAmount(event.amount());
		ledgerEntry.setCurrency(event.currency());
		ledgerEntry.setDirection(Direction.CREDIT);
		ledgerEntry.setCreatedAt(LocalDateTime.now());
		ledgerEntry.setEventType("wallet.credited.success");
		ledgerEntryRepository.save(ledgerEntry);
		
	}
	
	public void handlePaymentFailed(PaymentFailureEvent event) {
		
		if(ledgerEntryRepository.existsByTransactionIdAndDirectionAndEventType(event.transactionId(), Direction.DEBIT, "payment.failed")) {
			return;
		}
		LedgerEntry ledgerEntry = new LedgerEntry();
		ledgerEntry.setTransactionId(event.transactionId());
		ledgerEntry.setWalletId(event.sourceWalletId());
		ledgerEntry.setAmount(event.amount());
		ledgerEntry.setCurrency(event.currency());
		ledgerEntry.setDirection(Direction.DEBIT);
		ledgerEntry.setCreatedAt(LocalDateTime.now());
		ledgerEntry.setEventType("payment.failed");
		ledgerEntryRepository.save(ledgerEntry);
		
	}


}
