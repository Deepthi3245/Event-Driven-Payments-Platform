package com.walletra.ledger_service.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.walletra.ledger_service.enums.Direction;
import com.walletra.ledger_service.models.LedgerEntry;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {

	boolean existsByTransactionIdAndDirectionAndEventType(UUID transactionId, Direction directionm, String eventType);
}
