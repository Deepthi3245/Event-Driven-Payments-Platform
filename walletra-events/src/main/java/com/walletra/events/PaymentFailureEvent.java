package com.walletra.events;


import java.util.UUID;

public record PaymentFailureEvent(UUID transactionId, UUID sourceWalletId, UUID destinationWalletId, Double amount,
		String currency, String reason) {

}
