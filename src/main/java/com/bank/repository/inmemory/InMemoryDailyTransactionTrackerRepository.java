package com.bank.repository.inmemory;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

import com.bank.exception.DailyTrackerNotFoundException;
import com.bank.model.DailyTransactionTracker;
import com.bank.model.Money;
import com.bank.repository.DailyTransactionRepository;

public class InMemoryDailyTransactionTrackerRepository implements DailyTransactionRepository {

	private final ConcurrentHashMap<String, DailyTransactionTracker> store = new ConcurrentHashMap<>();

	@Override
	public Optional<DailyTransactionTracker> findByAccountAndDate(String accountNumber, LocalDate date) {
		return Optional.ofNullable(store.get(generateKey(accountNumber, date)));
	}

	@Override
	public DailyTransactionTracker save(DailyTransactionTracker tracker) {
		String accountNumber = tracker.getAccountNumber();
		LocalDate date = tracker.getDate();
		String key = generateKey(accountNumber, date);
		store.put(key, tracker);

		return tracker;
	}

	@Override
	public DailyTransactionTracker update(String accountNumber, LocalDate date,
	                                      UnaryOperator<DailyTransactionTracker> mutator) {
		String key = generateKey(accountNumber, date);
		return store.compute(key, (k, tracker) -> {
			if (Objects.isNull(tracker)) {
				// Create new tracker if it doesn't exist
				tracker = new DailyTransactionTracker(accountNumber, date, Money.ZERO);
			}
			return mutator.apply(tracker);
		});
	}

	private String generateKey(String accountNumber, LocalDate date) {
		return accountNumber + ":" + date.toString();
	}
}
