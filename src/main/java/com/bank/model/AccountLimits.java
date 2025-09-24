package com.bank.model;

import lombok.Getter;

@Getter
public class AccountLimits {

	private final Money dailyWithdrawalLimit;
	private final Money dailyDepositLimit;
	private final int dailyTransactionCount;
	private final Money minimumBalance;

	public AccountLimits(Money dailyWithdrawalLimit, Money dailyDepositLimit,
	                     int dailyTransactionCount, Money minimumBalance) {
		this.dailyWithdrawalLimit = dailyWithdrawalLimit;
		this.dailyDepositLimit = dailyDepositLimit;
		this.dailyTransactionCount = dailyTransactionCount;
		this.minimumBalance = minimumBalance;
	}

	public static AccountLimits forSavingsAccount() {
		return new AccountLimits(
				Money.of("500.00"), // $500 daily withdrawal limit
				null, // No deposit limit
				6, // Max 6 withdrawals per day
				Money.ZERO // No minimum balance
		);
	}

	public static AccountLimits forBusinessAccount() {
		return new AccountLimits(
				Money.of("5000.00"),
				null,
				50,
				Money.ZERO
		);
	}

	public static AccountLimits forStudentAccount() {
		return new AccountLimits(
				Money.of("500.00"),
				null,
				50,
				Money.ZERO
		);
	}

	public static AccountLimits forCheckingAccount() {
		return new AccountLimits(
				Money.of("1000.00"), // $1000 daily withdrawal limit
				null, // No deposit limit
				50,  // High withdrawal count
				Money.ZERO // No minimum balance 
		);
	}
}
