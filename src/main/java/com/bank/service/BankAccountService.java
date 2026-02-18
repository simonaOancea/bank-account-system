package com.bank.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.bank.exception.AccountNotFoundException;
import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.Money;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;

public class BankAccountService {

    private static final String ACCOUNT_NUMBER_GENERATOR_NULL_ERROR = "Account number generator cannot be null";
    private static final String ACCOUNT_NUMBER_NULL_OR_EMPTY_ERROR = "Account number cannot be null or empty";
    private static final String ACCOUNT_NOT_FOUND_ERROR = "Account not found: ";

    private final AccountRepository repository;
    private final TransactionRepository transactionRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    public BankAccountService(AccountRepository repository, TransactionRepository transactionRepository,
                              AccountNumberGenerator accountNumberGenerator) {
        this.repository = repository;
        this.transactionRepository = transactionRepository;

        if (Objects.isNull(accountNumberGenerator)) {
            throw new IllegalArgumentException(ACCOUNT_NUMBER_GENERATOR_NULL_ERROR);
        }
        this.accountNumberGenerator = accountNumberGenerator;
    }

    /**
     * Creates a new bank account for the customer with optional initial deposit.
     * 
     * @param customer the customer for whom the account is being created
     * @param initialDeposit optional initial deposit; if null or non-positive, account starts with zero balance
     * @return the generated account number for the new account
     */
    public String openAccount(Customer customer, Money initialDeposit) {
        String number = accountNumberGenerator.generateAccountNumber();
        Account account = new Account(number, customer);
        
        if (Objects.nonNull(initialDeposit) && initialDeposit.isPositive()) {
            account.deposit(initialDeposit);
            
        }
        
        repository.save(account);
        return number;
    }

    public Money deposit(String accountNumber, Money amount) {
        return repository.update(accountNumber, acc -> {
            acc.deposit(amount);
            recordTransaction(accountNumber, TransactionType.DEPOSIT, amount, acc.getBalance());
            return acc;
        }).getBalance();
    }

    public Money withdraw(String accountNumber, Money amount) {
        return repository.update(accountNumber, acc -> {
            acc.withdraw(amount);
            recordTransaction(accountNumber, TransactionType.WITHDRAW, amount, acc.getBalance());
            return acc;
        }).getBalance();
    }

    public Money getBalance(String accountNumber) {
        Account account = getAccount(accountNumber);
        return account.getBalance();
    }

    /**
     * Retrieves an account by account number with automatic whitespace trimming.
     * 
     * @param accountNumber the account number to look up (whitespace will be trimmed)
     * @return the account if found
     * @throws IllegalArgumentException if accountNumber is null or empty
     * @throws AccountNotFoundException if no account exists with the given number
     */
    public Account getAccount(String accountNumber) {
        if (Objects.isNull(accountNumber) || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException(ACCOUNT_NUMBER_NULL_OR_EMPTY_ERROR);
        }
        return repository.findByNumber(accountNumber.trim())
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND_ERROR + accountNumber));
    }

    public boolean accountExists(String accountNumber) {
        if (Objects.isNull(accountNumber) || accountNumber.trim().isEmpty()) {
            return false;
        }
        return repository.exists(accountNumber.trim());
    }

    public int getAccountCount() {
        return repository.count();
    }

    public List<Transaction> getTransactionHistory(String accountNumber, int limit) {
        getAccount(accountNumber);

        return transactionRepository.findByAccountNumber(accountNumber, limit);
    }


    private void recordTransaction(String accountNumber, TransactionType type, Money amount, Money balanceAfter) {
        Transaction recordedTransaction = new Transaction(generateTransactionId(), accountNumber,
                type, amount, balanceAfter, LocalDateTime.now());

        transactionRepository.save(recordedTransaction);
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
}