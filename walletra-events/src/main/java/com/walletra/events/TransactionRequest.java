package com.walletra.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionRequest(UUID sourceWalletId, 
		UUID destinationWalletId, Double amount, String currency, LocalDateTime timestamp) {

}