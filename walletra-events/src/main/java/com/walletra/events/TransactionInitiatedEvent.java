package com.walletra.events;

import java.util.UUID;
import java.time.LocalDateTime;

public record TransactionInitiatedEvent(
        UUID transactionId,
        UUID sourceWalletId,
        UUID destinationWalletId,
        Double amount,
        String currency,
        LocalDateTime timestamp
) {}