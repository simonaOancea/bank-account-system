package com.bank.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import com.bank.exception.DailyLimitException;

class AccountTest {

    private static final String TEST_ACCOUNT_NUMBER = "12345";
    private static final String ALTERNATIVE_ACCOUNT_NUMBER = "67890";
    private static final String TEST_CUSTOMER_FIRST_NAME = "John";
    private static final String TEST_CUSTOMER_LAST_NAME = "Doe";
    
    private static final String ACCOUNT_NUMBER_NULL_OR_EMPTY_ERROR = "Account number cannot be null or empty";
    private static final String CUSTOMER_NULL_ERROR = "Customer cannot be null";
    private static final String DEPOSIT_AMOUNT_NULL_ERROR = "Deposit amount cannot be null";
    private static final String DEPOSIT_AMOUNT_POSITIVE_ERROR = "Deposit amount must be positive";
    private static final String WITHDRAWAL_AMOUNT_NULL_ERROR = "Withdrawal amount cannot be null";
    private static final String WITHDRAWAL_AMOUNT_POSITIVE_ERROR = "Withdrawal amount must be positive";
    private static final String INSUFFICIENT_FUNDS_ERROR = "Insufficient funds for withdrawal";

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer(TEST_CUSTOMER_FIRST_NAME, TEST_CUSTOMER_LAST_NAME);
    }

    @Test
    @DisplayName("Should create account with valid parameters")
    void shouldCreateAccountWithValidParameters() {
        Account account = new Account(TEST_ACCOUNT_NUMBER, customer, AccountType.CHECKING);
        
        assertEquals(TEST_ACCOUNT_NUMBER, account.getAccountNumber());
        assertEquals(customer, account.getCustomer());
        assertEquals(Money.ZERO, account.getBalance());
    }

    @Test
    @DisplayName("Should trim account number whitespace")
    void shouldTrimAccountNumberWhitespace() {
        Account account = new Account("  " + TEST_ACCOUNT_NUMBER + "  ", customer, AccountType.CHECKING);
        
        assertEquals(TEST_ACCOUNT_NUMBER, account.getAccountNumber());
    }

    @Test
    @DisplayName("Should throw exception for null account number")
    void shouldThrowExceptionForNullAccountNumber() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Account(null, customer, AccountType.CHECKING)
        );
        assertEquals(ACCOUNT_NUMBER_NULL_OR_EMPTY_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for empty account number")
    void shouldThrowExceptionForEmptyAccountNumber() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Account("", customer, AccountType.CHECKING)
        );
        assertEquals(ACCOUNT_NUMBER_NULL_OR_EMPTY_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for null customer")
    void shouldThrowExceptionForNullCustomer() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Account(TEST_ACCOUNT_NUMBER, null, AccountType.CHECKING)
        );
        assertEquals(CUSTOMER_NULL_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should deposit valid amount")
    void shouldDepositValidAmount() {
        Account account = new Account(TEST_ACCOUNT_NUMBER, customer, AccountType.CHECKING);
        Money depositAmount = Money.of("100.50");
        
        account.deposit(depositAmount);
        
        assertEquals(depositAmount, account.getBalance());
    }

    @Test
    @DisplayName("Should handle multiple deposits")
    void shouldHandleMultipleDeposits() {
        Account account = new Account(TEST_ACCOUNT_NUMBER, customer, AccountType.CHECKING);
        
        account.deposit(Money.of("100.00"));
        account.deposit(Money.of("50.25"));
        
        assertEquals(Money.of("150.25"), account.getBalance());
    }

    @Test
    @DisplayName("Should throw exception for null deposit amount")
    void shouldThrowExceptionForNullDepositAmount() {
        Account account = new Account(TEST_ACCOUNT_NUMBER, customer, AccountType.CHECKING);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> account.deposit(null)
        );
        assertEquals(DEPOSIT_AMOUNT_NULL_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for negative deposit amount")
    void shouldThrowExceptionForNegativeDepositAmount() {
        Account account = new Account(TEST_ACCOUNT_NUMBER, customer, AccountType.CHECKING);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> account.deposit(Money.of("-10.00"))
        );
        assertEquals(DEPOSIT_AMOUNT_POSITIVE_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for zero deposit amount")
    void shouldThrowExceptionForZeroDepositAmount() {
        Account account = new Account(TEST_ACCOUNT_NUMBER, customer, AccountType.CHECKING);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> account.deposit(Money.ZERO)
        );
        assertEquals(DEPOSIT_AMOUNT_POSITIVE_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should withdraw valid amount")
    void shouldWithdrawValidAmount() {
        Account account = new Account(TEST_ACCOUNT_NUMBER, customer, AccountType.CHECKING);
        account.deposit(Money.of("100.00"));
        
        account.withdraw(Money.of("50.00"));
        
        assertEquals(Money.of("50.00"), account.getBalance());
    }

    @Test
    @DisplayName("Should handle multiple withdrawals")
    void shouldHandleMultipleWithdrawals() {
        Account account = new Account(TEST_ACCOUNT_NUMBER, customer, AccountType.CHECKING);
        account.deposit(Money.of("200.00"));
        
        account.withdraw(Money.of("50.00"));
        account.withdraw(Money.of("25.50"));
        
        assertEquals(Money.of("124.50"), account.getBalance());
    }

    @Test
    @DisplayName("Should throw exception for null withdrawal amount")
    void shouldThrowExceptionForNullWithdrawalAmount() {
        Account account = new Account(TEST_ACCOUNT_NUMBER, customer, AccountType.CHECKING);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> account.withdraw(null)
        );
        assertEquals(WITHDRAWAL_AMOUNT_NULL_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for negative withdrawal amount")
    void shouldThrowExceptionForNegativeWithdrawalAmount() {
        Account account = new Account(TEST_ACCOUNT_NUMBER, customer, AccountType.CHECKING);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> account.withdraw(Money.of("-10.00"))
        );
        assertEquals(WITHDRAWAL_AMOUNT_POSITIVE_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for zero withdrawal amount")
    void shouldThrowExceptionForZeroWithdrawalAmount() {
        Account account = new Account(TEST_ACCOUNT_NUMBER, customer, AccountType.CHECKING);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> account.withdraw(Money.ZERO)
        );
        assertEquals(WITHDRAWAL_AMOUNT_POSITIVE_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for insufficient funds")
    void shouldThrowExceptionForInsufficientFunds() {
        Account account = new Account(TEST_ACCOUNT_NUMBER, customer, AccountType.CHECKING);
        account.deposit(Money.of("50.00"));
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> account.withdraw(Money.of("100.00"))
        );
        assertEquals(INSUFFICIENT_FUNDS_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should withdraw exact balance amount")
    void shouldWithdrawExactBalanceAmount() {
        Account account = new Account(TEST_ACCOUNT_NUMBER, customer, AccountType.CHECKING);
        Money amount = Money.of("100.00");
        account.deposit(amount);
        
        account.withdraw(amount);
        
        assertEquals(Money.ZERO, account.getBalance());
    }

    @Test
    @DisplayName("Should implement equals based on account number")
    void shouldImplementEqualsBasedOnAccountNumber() {
        Account account1 = new Account("12345", customer, AccountType.CHECKING);
        Account account2 = new Account(TEST_ACCOUNT_NUMBER, new Customer("Jane", "Smith"), AccountType.CHECKING);
        Account account3 = new Account(ALTERNATIVE_ACCOUNT_NUMBER, customer, AccountType.CHECKING);
        
        assertEquals(account1, account2);
        assertNotEquals(account1, account3);
        assertNotEquals(null, account1);
        assertNotEquals("not an account", account1);
    }

    @Test
    @DisplayName("Should implement hashCode based on account number")
    void shouldImplementHashCodeBasedOnAccountNumber() {
        Account account1 = new Account("12345", customer, AccountType.CHECKING);
        Account account2 = new Account(TEST_ACCOUNT_NUMBER, new Customer("Jane", "Smith"), AccountType.CHECKING);
        
        assertEquals(account1.hashCode(), account2.hashCode());
    }

    @Test
    @DisplayName("Should maintain correct scale for decimal operations")
    void shouldMaintainCorrectScaleForDecimalOperations() {
        Account account = new Account(TEST_ACCOUNT_NUMBER, customer, AccountType.CHECKING);
        
        account.deposit(Money.of("10.1"));
        account.deposit(Money.of("20.23"));
        
        assertEquals(2, account.getBalance().getAmount().scale());
        assertEquals(Money.of("30.33"), account.getBalance());
    }

    @Test
    @DisplayName("Should check sufficient funds correctly")
    void shouldCheckSufficientFundsCorrectly() {
        Account account = new Account(TEST_ACCOUNT_NUMBER, customer, AccountType.CHECKING);
        account.deposit(Money.of("100.00"));
        
        assertTrue(account.hasSufficientFunds(Money.of("50.00")));
        assertTrue(account.hasSufficientFunds(Money.of("100.00")));
        assertFalse(account.hasSufficientFunds(Money.of("100.01")));
        assertFalse(account.hasSufficientFunds(Money.of("200.00")));
    }
    @Test
    @DisplayName("Should format balance correctly")
    void shouldFormatBalanceCorrectly() {
        Account account = new Account(TEST_ACCOUNT_NUMBER, customer, AccountType.CHECKING);
        account.deposit(Money.of("123.45"));
        
        assertEquals("$123.45", account.getFormattedBalance());
    }

    @Test
    @DisplayName("Should throw DailyLimitException when SAVINGS account withdrawal exceeds daily limit")
    void shouldThrowDailyLimitExceptionWhenSavingsAccountWithdrawalExceedsDailyLimit() {
        Account account = new Account(TEST_ACCOUNT_NUMBER, customer, AccountType.SAVINGS);
        account.deposit(Money.of("1000"));

        DailyLimitException exception = assertThrows(DailyLimitException.class,
                () -> account.withdraw(Money.of("600")));

        assertEquals("Withdrawal amount exceeds daily limit of $500.00", exception.getMessage());
    }
}