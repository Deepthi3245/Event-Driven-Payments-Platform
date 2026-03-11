package com.walletra.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record WalletCreditedFailedEvent(
	    UUID transactionId,
	    UUID sourceWalletId,
	    UUID destinationWalletId,
	    Double amount,
	    String currency,
	    String reason,
	    LocalDateTime occurredAt
	) {}
