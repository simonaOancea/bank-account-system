package com.bank.model;

import java.util.Objects;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Bank account entity with immutable identity and mutable balance.
 * Uses Money value object for precise monetary calculations.
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Account {
    
    private static final String ACCOUNT_NUMBER_ERROR = "Account number cannot be null or empty";
    private static final String CUSTOMER_ERROR = "Customer cannot be null";
    private static final String DEPOSIT_AMOUNT_NULL_ERROR = "Deposit amount cannot be null";
    private static final String DEPOSIT_AMOUNT_POSITIVE_ERROR = "Deposit amount must be positive";
    private static final String WITHDRAWAL_AMOUNT_NULL_ERROR = "Withdrawal amount cannot be null";
    private static final String WITHDRAWAL_AMOUNT_POSITIVE_ERROR = "Withdrawal amount must be positive";
    private static final String INSUFFICIENT_FUNDS_ERROR = "Insufficient funds for withdrawal";
    
    @EqualsAndHashCode.Include
    private final String accountNumber;
    private final Customer customer;

    private Money balance;

    public Account(String accountNumber, Customer customer) {
        this.accountNumber = validateAndTrimAccountNumber(accountNumber);
        this.customer = validateCustomer(customer);
        this.balance = Money.ZERO;
    }

    /**
     * Deposits money into the account.
     * @param amount the amount to deposit (must be positive)
     * @throws IllegalArgumentException if amount is null or not positive
     */
    public void deposit(Money amount) {
        if (Objects.isNull(amount)) {
            throw new IllegalArgumentException(DEPOSIT_AMOUNT_NULL_ERROR);
        }
        if (!amount.isPositive()) {
            throw new IllegalArgumentException(DEPOSIT_AMOUNT_POSITIVE_ERROR);
        }
        this.balance = this.balance.add(amount);
    }

    /**
     * Withdraws money from the account.
     * @param amount the amount to withdraw (must be positive and not exceed balance)
     * @throws IllegalArgumentException if amount is invalid or insufficient funds
     */
    public void withdraw(Money amount) {
        if (Objects.isNull(amount)) {
            throw new IllegalArgumentException(WITHDRAWAL_AMOUNT_NULL_ERROR);
        }
        if (!amount.isPositive()) {
            throw new IllegalArgumentException(WITHDRAWAL_AMOUNT_POSITIVE_ERROR);
        }
        if (amount.isGreaterThan(this.balance)) {
            throw new IllegalArgumentException(INSUFFICIENT_FUNDS_ERROR);
        }
        this.balance = this.balance.subtract(amount);
    }
    
    /**
     * Checks if the account has sufficient funds for the specified amount.
     * @param amount the amount to check
     * @return true if balance is greater than or equal to amount
     */
    public boolean hasSufficientFunds(Money amount) {
        return this.balance.isGreaterThanOrEqualTo(amount);
    }
    
    /**
     * Returns the formatted balance as a string.
     * @return formatted balance like "$123.45"
     */
    public String getFormattedBalance() {
        return balance.toFormattedString();
    }
    
    private static String validateAndTrimAccountNumber(String accountNumber) {
        if (Objects.isNull(accountNumber) || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException(ACCOUNT_NUMBER_ERROR);
        }
        return accountNumber.trim();
    }
    
    private static Customer validateCustomer(Customer customer) {
        if (Objects.isNull(customer)) {
            throw new IllegalArgumentException(CUSTOMER_ERROR);
        }
        return customer;
    }
}