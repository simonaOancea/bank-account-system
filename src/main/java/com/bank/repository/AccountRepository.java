package com.bank.repository;

import java.util.Optional;
import java.util.function.UnaryOperator;

import com.bank.model.Account;

public interface AccountRepository {

	Optional<Account> findByNumber(String accountNumber);

	/** Create or replace the account (idempotent save). */
	Account save(Account account);

	/** Atomically load & mutate a single account. Throws if missing. */
	Account update(String accountNumber, UnaryOperator<Account> mutator);

	boolean exists(String accountNumber);

	int count();
}
