package com.bank.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.UnaryOperator;

import com.bank.model.DailyTransactionTracker;

public interface DailyTransactionRepository {
	Optional<DailyTransactionTracker> findByAccountAndDate(String accountNumber, LocalDate date);
	DailyTransactionTracker save(DailyTransactionTracker tracker);
	DailyTransactionTracker update(String accountNumber, LocalDate date, UnaryOperator<DailyTransactionTracker> mutator);
}
