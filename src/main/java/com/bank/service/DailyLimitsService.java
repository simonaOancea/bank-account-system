package com.bank.service;

import java.time.LocalDate;
import java.util.Objects;

import com.bank.exception.DailyLimitException;
import com.bank.model.AccountLimits;
import com.bank.model.DailyTransactionTracker;
import com.bank.model.Money;
import com.bank.repository.DailyTransactionRepository;

public class DailyLimitsService {

	private final DailyTransactionRepository dailyTransactionRepository;

	public DailyLimitsService(DailyTransactionRepository dailyTransactionRepository) {
		this.dailyTransactionRepository = dailyTransactionRepository;
	}

	public void validateWithdrawal(String accountNumber, Money withdrawalAmount, AccountLimits limits) {
		validateWithdrawalInputs(accountNumber, withdrawalAmount);

		if (Objects.isNull(limits)) {
			return; // no limits to enforce
		}

		LocalDate today = LocalDate.now();

		// Get or create today's tracker
		DailyTransactionTracker tracker = dailyTransactionRepository
				.findByAccountAndDate(accountNumber.trim(), today)
				.orElse(new DailyTransactionTracker(accountNumber.trim(), today, Money.ZERO));

		// Check daily withdrawal account limit
		Money potentialTotal = tracker.getTotalWithdrawals().add(withdrawalAmount);
		if (Objects.nonNull(limits.getDailyWithdrawalLimit()) &&
				potentialTotal.isGreaterThan(limits.getDailyWithdrawalLimit())) {
			throw new DailyLimitException(String.format(
					"Daily withdrawal limit exceeded. Current: %s, Attempting: %s, Limit: %s",
					tracker.getTotalWithdrawals().toFormattedString(),
					withdrawalAmount.toFormattedString(),
					limits.getDailyWithdrawalLimit().toFormattedString()
			));
		}

		// Check daily withdrawal count limit
		if (tracker.getWithdrawalCount() >= limits.getDailyTransactionCount()) {
			throw new DailyLimitException(String.format("Daily withdrawal count limit exceeded. Current: %d, Limit: %d",
					tracker.getWithdrawalCount(), limits.getDailyTransactionCount()));
		}
	}

	public void recordWithdrawal(String accountNumber, Money withdrawalAmount) {
		validateWithdrawalInputs(accountNumber, withdrawalAmount);

		LocalDate today = LocalDate.now();
		String trimmedAccountNumber = accountNumber.trim();

		// Atomic update: get existing tracker OR create new one, then record withdrawal
		dailyTransactionRepository.update(trimmedAccountNumber, today, tracker -> {
			if (tracker == null) {
				// Create new tracker if none exists
				tracker = new DailyTransactionTracker(trimmedAccountNumber, today, Money.ZERO);
			}
			tracker.recordWithdrawal(withdrawalAmount);
			return tracker;
		});
	}

	private void validateWithdrawalInputs(String accountNumber, Money withdrawalAmount) {
		if (Objects.isNull(accountNumber) || accountNumber.trim().isEmpty()) {
			throw new IllegalArgumentException("Account number cannot be null or empty");
		}

		if (Objects.isNull(withdrawalAmount)) {
			throw new IllegalArgumentException("Withdrawal amount cannot be null");
		}

		if (!withdrawalAmount.isPositive()) {
			throw new IllegalArgumentException("Withdrawal amount must be positive");
		}
	}
}
