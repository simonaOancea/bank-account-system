package com.bank.model;

import java.time.LocalDateTime;

public record Transaction(String transactionId, String accountNumber, TransactionType type, Money amount,
                          Money afterAmount, LocalDateTime timestamp) {

	public String getTransactionId() {
		return this.transactionId;
	}

	public String getType() {
		return this.type.toString();
	}

	public Money getAmount() {
		return this.amount;
	}
}
