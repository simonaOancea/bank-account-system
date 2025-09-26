package com.bank.model;

import lombok.Getter;

@Getter
public enum TransactionType {
	DEPOSIT("Deposit"),
	WITHDRAW("Withdraw");

	private final String displayName;

	TransactionType(String displayName) {
		this.displayName = displayName;
	}
}
