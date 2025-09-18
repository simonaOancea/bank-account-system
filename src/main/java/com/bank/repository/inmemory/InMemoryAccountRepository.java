package com.bank.repository.inmemory;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

import com.bank.exception.AccountNotFoundException;
import com.bank.model.Account;
import com.bank.repository.AccountRepository;

public class InMemoryAccountRepository implements AccountRepository {

	private static final String ACCOUNT_NOT_FOUND_ERROR = "Account not found: ";
	
	private final ConcurrentHashMap<String, Account> store = new ConcurrentHashMap<>();

	@Override
	public Optional<Account> findByNumber(String accountNumber) {
		return Optional.ofNullable(store.get(accountNumber));
	}

	@Override
	public Account save(Account account) {
		store.put(account.getAccountNumber(), account);
		return account;
	}

	/**
	 * Atomically updates an account using ConcurrentHashMap.compute() to ensure thread safety.
	 * The entire read-modify-write operation is atomic, preventing race conditions during
	 * concurrent deposits/withdrawals on the same account.
	 * 
	 * @param accountNumber the account to update
	 * @param mutator function that modifies the account (e.g., deposit, withdraw)
	 * @return the updated account
	 * @throws AccountNotFoundException if the account does not exist
	 */
	@Override
	public Account update(String accountNumber, UnaryOperator<Account> mutator) {
		return store.compute(accountNumber, (k, acc) -> {
			if (Objects.isNull(acc)) {
				throw new AccountNotFoundException(ACCOUNT_NOT_FOUND_ERROR + accountNumber);
			}
			return mutator.apply(acc);
		});
	}

	@Override
	public boolean exists(String accountNumber) {
		return store.containsKey(accountNumber);
	}

	@Override
	public int count() {
		return store.size();
	}
}
