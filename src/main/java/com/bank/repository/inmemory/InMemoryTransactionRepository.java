package com.bank.repository.inmemory;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.bank.model.Transaction;
import com.bank.repository.TransactionRepository;

public class InMemoryTransactionRepository implements TransactionRepository {

	private final ConcurrentHashMap<String, Transaction> transactionStore = new ConcurrentHashMap<>();

	@Override
	public Transaction save(Transaction transaction) {
		transactionStore.put(transaction.transactionId(), transaction);

		return transaction;
	}

	@Override
	public List<Transaction> findByAccountNumber(String accountNumber, int limit) {
		return transactionStore.values().stream()
				.filter(transaction -> accountNumber.equals(transaction.accountNumber()))
				.sorted(Comparator.comparing(Transaction::timestamp).reversed()) // Most recent first
				.limit(limit)
				.collect(Collectors.toList());
	}
}
