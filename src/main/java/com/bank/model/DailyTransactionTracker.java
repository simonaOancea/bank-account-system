package com.bank.model;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class DailyTransactionTracker {

	private static final int TOTAL_WITHDRAWALS_INITIALIZER = 0;

	private final String accountNumber;
	private final LocalDate date;
	private Money totalWithdrawals;
	private int withdrawalCount;

	public DailyTransactionTracker(String accountNumber, LocalDate date, Money totalWithdrawals) {
		this.accountNumber = accountNumber;
		this.date = date;
		this.totalWithdrawals = totalWithdrawals;
		this.withdrawalCount = TOTAL_WITHDRAWALS_INITIALIZER;
	}

	public void recordWithdrawal(Money amount) {
		this.totalWithdrawals = this.totalWithdrawals.add(amount);
		this.withdrawalCount++;
	}
}
