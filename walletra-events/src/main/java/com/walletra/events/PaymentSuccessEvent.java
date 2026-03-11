package com.walletra.events;

import java.util.UUID;

public record PaymentSuccessEvent(
		UUID transactionId, 
		UUID sourceWalletId, UUID destinationWalletId, Double amount, String currency) {

}
