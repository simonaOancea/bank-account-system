package com.bank.service;

import java.util.Objects;

import com.bank.exception.AccountNotFoundException;
import com.bank.model.Account;
import com.bank.model.AccountType;
import com.bank.model.Customer;
import com.bank.model.Money;
import com.bank.repository.AccountRepository;

public class BankAccountService {

    private static final String ACCOUNT_NUMBER_GENERATOR_NULL_ERROR = "Account number generator cannot be null";
    private static final String ACCOUNT_NUMBER_NULL_OR_EMPTY_ERROR = "Account number cannot be null or empty";
    private static final String ACCOUNT_NOT_FOUND_ERROR = "Account not found: ";
    private static final String INSUFFICIENT_FUNDS_TRANSFER_ERROR = "Insufficient funds for transfer";
    private static final String FROM_ACCOUNT_NULL_OR_EMPTY_ERROR = "From account number cannot be null or empty";
    private static final String TO_ACCOUNT_NULL_OR_EMPTY_ERROR = "To account number cannot be null or empty";
    private static final String TRANSFER_AMOUNT_NULL_ERROR = "Transfer amount cannot be null";
    private static final String TRANSFER_AMOUNT_POSITIVE_ERROR = "Transfer amount must be positive";
    private static final String SAME_ACCOUNT_TRANSFER_ERROR = "Cannot transfer to the same account";
    private static final String SAVINGS_MIN_DEPOSIT_ERROR = "SAVINGS accounts require a minimum opening deposit of $500.00";
    private static final String SAVINGS_ACCOUNT_MIN_DEPOSIT = "500.00";

    private final AccountRepository repository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final DailyLimitsService dailyLimitsService;

    public BankAccountService(AccountRepository repository, AccountNumberGenerator accountNumberGenerator, DailyLimitsService dailyLimitsService) {
        this.repository = repository;

        if (Objects.isNull(accountNumberGenerator)) {
            throw new IllegalArgumentException(ACCOUNT_NUMBER_GENERATOR_NULL_ERROR);
        }
        this.accountNumberGenerator = accountNumberGenerator;
        this.dailyLimitsService = dailyLimitsService;
    }

    /**
     * Creates a new bank account for the customer with specified account type and optional initial deposit.
     * 
     * @param customer the customer for whom the account is being created
     * @param accountType the type of account to create (CHECKING, SAVINGS, etc.)
     * @param initialDeposit optional initial deposit; if null or non-positive, account starts with zero balance.
     *                      Note: SAVINGS accounts require a minimum deposit of $500.00
     * @return the generated account number for the new account
     * @throws IllegalArgumentException if SAVINGS account is created with insufficient initial deposit
     */
    public String openAccount(Customer customer, AccountType accountType, Money initialDeposit) {
        // Normalize null deposit to zero for consistent handling
        Money effectiveDeposit = Objects.isNull(initialDeposit) ? Money.ZERO : initialDeposit;
        
        validateAccountOpening(accountType, effectiveDeposit);
        
        String number = accountNumberGenerator.generateAccountNumber();
        Account account = new Account(number, customer, accountType);
        
        if (effectiveDeposit.isPositive()) {
            account.deposit(effectiveDeposit);
        }
        
        repository.save(account);
        return number;
    }

    public Money deposit(String accountNumber, Money amount) {
        return repository.update(accountNumber, acc -> {
            acc.deposit(amount);
            return acc;
        }).getBalance();
    }

    public Money withdraw(String accountNumber, Money amount) {
        return repository.update(accountNumber, account -> {
            // All operations happen atomically under ConcurrentHashMap.compute() lock
            dailyLimitsService.validateWithdrawal(accountNumber, amount, account.getLimits());
            account.withdraw(amount);
            dailyLimitsService.recordWithdrawal(accountNumber, amount);
            return account;
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

    public Money transfer(String fromAccount, String toAccount, Money amountToTransfer) {
        validateTransferInputs(fromAccount, toAccount, amountToTransfer);

        // Trim account numbers for consistent handling
        String trimmedFromAccount = fromAccount.trim();
        String trimmedToAccount = toAccount.trim();

        // Check sufficient funds
        Money currentBalance = getBalance(trimmedFromAccount);
        if (!currentBalance.isGreaterThanOrEqualTo(amountToTransfer)) {
            throw new IllegalStateException(INSUFFICIENT_FUNDS_TRANSFER_ERROR);
        }

        // Perfect atomic transfer
        withdraw(trimmedFromAccount, amountToTransfer);
        deposit(trimmedToAccount, amountToTransfer);

        return getBalance(trimmedFromAccount);
    }

    private void validateAccountOpening(AccountType accountType, Money effectiveDeposit) {
        if (accountType == AccountType.SAVINGS) {
            if (effectiveDeposit.isLessThan(Money.of(SAVINGS_ACCOUNT_MIN_DEPOSIT))) {
                throw new IllegalArgumentException(SAVINGS_MIN_DEPOSIT_ERROR);
            }
        }
    }

    private void validateTransferInputs(String fromAccount, String toAccount, Money amountToTransfer) {
        // Check account numbers are not null/empty
        if (Objects.isNull(fromAccount) || fromAccount.trim().isEmpty()) {
            throw new IllegalArgumentException(FROM_ACCOUNT_NULL_OR_EMPTY_ERROR);
        }

        if (Objects.isNull(toAccount) || toAccount.trim().isEmpty()) {
            throw new IllegalArgumentException(TO_ACCOUNT_NULL_OR_EMPTY_ERROR);
        }

        // Check transfer amount
        if (Objects.isNull(amountToTransfer)) {
            throw new IllegalArgumentException(TRANSFER_AMOUNT_NULL_ERROR);
        }

        if (!amountToTransfer.isPositive()) {
            throw new IllegalArgumentException(TRANSFER_AMOUNT_POSITIVE_ERROR);
        }

        // Check accounts are different
        if (fromAccount.trim().equals(toAccount.trim())) {
            throw new IllegalArgumentException(SAME_ACCOUNT_TRANSFER_ERROR);
        }

        // Check both accounts exist (this will throw AccountNotFoundException if they don't)
        if (!accountExists(fromAccount)) {
            throw new AccountNotFoundException(ACCOUNT_NOT_FOUND_ERROR + fromAccount);
        }

        if (!accountExists(toAccount)) {
            throw new AccountNotFoundException(ACCOUNT_NOT_FOUND_ERROR + toAccount);
        }
    }

    public int getAccountCount() {
        return repository.count();
    }
}