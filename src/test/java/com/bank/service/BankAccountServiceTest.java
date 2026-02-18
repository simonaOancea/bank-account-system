package com.bank.service;

import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import com.bank.repository.TransactionRepository;
import com.bank.repository.inmemory.InMemoryAccountRepository;
import com.bank.exception.AccountNotFoundException;
import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.Money;
import com.bank.repository.AccountRepository;
import com.bank.repository.inmemory.InMemoryTransactionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    private static final String TEST_ACCOUNT_NUMBER = "12345";
    private static final String ALTERNATIVE_ACCOUNT_NUMBER = "67890";
    private static final String NON_EXISTENT_ACCOUNT_NUMBER = "99999";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String ALTERNATIVE_FIRST_NAME = "Jane";
    private static final String ALTERNATIVE_LAST_NAME = "Smith";
    private static final String ACCOUNT_NUMBER_WITH_WHITESPACE = "  12345  ";
    
    private static final String AMOUNT_100_50 = "100.50";
    private static final String AMOUNT_200_00 = "200.00";
    private static final String AMOUNT_50_75 = "50.75";
    private static final String AMOUNT_149_25 = "149.25";
    private static final String AMOUNT_75_25 = "75.25";
    private static final String AMOUNT_100_00 = "100.00";
    private static final String AMOUNT_50_00 = "50.00";
    private static final String AMOUNT_1000_00 = "1000.00";
    private static final String AMOUNT_10_00 = "10.00";
    
    private static final String ACCOUNT_NUMBER_GENERATOR_NULL_ERROR = "Account number generator cannot be null";
    private static final String ACCOUNT_NOT_FOUND_ERROR_PREFIX = "Account not found: ";
    private static final String ACCOUNT_NUMBER_NULL_OR_EMPTY_ERROR = "Account number cannot be null or empty";

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    private AccountRepository repository;
    private TransactionRepository transactionRepository;
    private BankAccountService bankService;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAccountRepository();
        transactionRepository = new InMemoryTransactionRepository();
        bankService = new BankAccountService(repository, transactionRepository, accountNumberGenerator);
    }

    @Test
    @DisplayName("Should throw exception for null account number generator")
    void shouldThrowExceptionForNullAccountNumberGenerator() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new BankAccountService(repository, transactionRepository, null)
        );
        assertEquals(ACCOUNT_NUMBER_GENERATOR_NULL_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should create new account successfully")
    void shouldCreateNewAccountSuccessfully() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);
        Customer customer = new Customer(TEST_FIRST_NAME, TEST_LAST_NAME);
        
        String accountNumber = bankService.openAccount(customer, null);
        
        assertNotNull(accountNumber);
        assertEquals(TEST_ACCOUNT_NUMBER, accountNumber);

        Account account = bankService.getAccount(accountNumber);
        assertEquals(TEST_FIRST_NAME, account.getCustomer().firstName());
        assertEquals(TEST_LAST_NAME, account.getCustomer().lastName());
        assertEquals(Money.ZERO, account.getBalance());
        
        verify(accountNumberGenerator).generateAccountNumber();
    }

    @Test
    @DisplayName("Should deposit money to existing account")
    void shouldDepositMoneyToExistingAccount() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);
        Customer customer = new Customer(TEST_FIRST_NAME, TEST_LAST_NAME);
        String accountNumber = bankService.openAccount(customer, null);
        Money depositAmount = Money.of(AMOUNT_100_50);
        
        Money newBalance = bankService.deposit(accountNumber, depositAmount);
        
        assertEquals(depositAmount, newBalance);
        assertEquals(depositAmount, bankService.getBalance(accountNumber));
    }

    @Test
    @DisplayName("Should withdraw money from existing account")
    void shouldWithdrawMoneyFromExistingAccount() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);
        Customer customer = new Customer(TEST_FIRST_NAME, TEST_LAST_NAME);
        String accountNumber = bankService.openAccount(customer, null);
        bankService.deposit(accountNumber, Money.of(AMOUNT_200_00));
        Money withdrawAmount = Money.of(AMOUNT_50_75);
        
        Money newBalance = bankService.withdraw(accountNumber, withdrawAmount);
        
        Money expectedBalance = Money.of(AMOUNT_149_25);
        assertEquals(expectedBalance, newBalance);
        assertEquals(expectedBalance, bankService.getBalance(accountNumber));
    }

    @Test
    @DisplayName("Should get balance for existing account")
    void shouldGetBalanceForExistingAccount() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);
        Customer customer = new Customer(TEST_FIRST_NAME, TEST_LAST_NAME);
        String accountNumber = bankService.openAccount(customer, null);
        Money depositAmount = Money.of(AMOUNT_75_25);
        bankService.deposit(accountNumber, depositAmount);
        
        Money balance = bankService.getBalance(accountNumber);
        
        assertEquals(depositAmount, balance);
    }

    @Test
    @DisplayName("Should throw exception when depositing to non-existent account")
    void shouldThrowExceptionWhenDepositingToNonExistentAccount() {
        AccountNotFoundException exception = assertThrows(
            AccountNotFoundException.class,
            () -> bankService.deposit(NON_EXISTENT_ACCOUNT_NUMBER, Money.of(AMOUNT_100_00))
        );
        assertEquals(ACCOUNT_NOT_FOUND_ERROR_PREFIX + NON_EXISTENT_ACCOUNT_NUMBER, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when withdrawing from non-existent account")
    void shouldThrowExceptionWhenWithdrawingFromNonExistentAccount() {
        AccountNotFoundException exception = assertThrows(
            AccountNotFoundException.class,
            () -> bankService.withdraw(NON_EXISTENT_ACCOUNT_NUMBER, Money.of(AMOUNT_50_00))
        );
        assertEquals(ACCOUNT_NOT_FOUND_ERROR_PREFIX + NON_EXISTENT_ACCOUNT_NUMBER, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when getting balance for non-existent account")
    void shouldThrowExceptionWhenGettingBalanceForNonExistentAccount() {
        AccountNotFoundException exception = assertThrows(
            AccountNotFoundException.class,
            () -> bankService.getBalance(NON_EXISTENT_ACCOUNT_NUMBER)
        );
        assertEquals(ACCOUNT_NOT_FOUND_ERROR_PREFIX + NON_EXISTENT_ACCOUNT_NUMBER, exception.getMessage());
    }

    @Test
    @DisplayName("Should get account by account number")
    void shouldGetAccountByAccountNumber() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);
        Customer customer = new Customer(TEST_FIRST_NAME, TEST_LAST_NAME);
        String accountNumber = bankService.openAccount(customer, null);
        
        Account retrievedAccount = bankService.getAccount(accountNumber);
        
        assertEquals(accountNumber, retrievedAccount.getAccountNumber());
        assertEquals("John", retrievedAccount.getCustomer().firstName());
        assertEquals("Doe", retrievedAccount.getCustomer().lastName());
    }

    @Test
    @DisplayName("Should throw exception for null account number in getAccount")
    void shouldThrowExceptionForNullAccountNumberInGetAccount() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bankService.getAccount(null)
        );
        assertEquals(ACCOUNT_NUMBER_NULL_OR_EMPTY_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for empty account number in getAccount")
    void shouldThrowExceptionForEmptyAccountNumberInGetAccount() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bankService.getAccount("")
        );
        assertEquals(ACCOUNT_NUMBER_NULL_OR_EMPTY_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should handle whitespace in account numbers")
    void shouldHandleWhitespaceInAccountNumbers() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);
        Customer customer = new Customer(TEST_FIRST_NAME, TEST_LAST_NAME);
	    bankService.openAccount(customer, null);

	    Account account = bankService.getAccount(ACCOUNT_NUMBER_WITH_WHITESPACE);
        
        assertEquals(TEST_ACCOUNT_NUMBER, account.getAccountNumber());
    }

    @Test
    @DisplayName("Should check if account exists")
    void shouldCheckIfAccountExists() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);
        Customer customer = new Customer(TEST_FIRST_NAME, TEST_LAST_NAME);
        String accountNumber = bankService.openAccount(customer, null);
        
        assertTrue(bankService.accountExists(accountNumber));
        assertFalse(bankService.accountExists(NON_EXISTENT_ACCOUNT_NUMBER));
        assertFalse(bankService.accountExists(null));
        assertFalse(bankService.accountExists(""));
    }

    @Test
    @DisplayName("Should get account count")
    void shouldGetAccountCount() {
        when(accountNumberGenerator.generateAccountNumber())
            .thenReturn(TEST_ACCOUNT_NUMBER)
            .thenReturn(ALTERNATIVE_ACCOUNT_NUMBER);
        
        assertEquals(0, bankService.getAccountCount());
        
        Customer customer1 = new Customer(TEST_FIRST_NAME, TEST_LAST_NAME);
        bankService.openAccount(customer1, null);
        assertEquals(1, bankService.getAccountCount());
        
        Customer customer2 = new Customer(ALTERNATIVE_FIRST_NAME, ALTERNATIVE_LAST_NAME);
        bankService.openAccount(customer2, null);
        assertEquals(2, bankService.getAccountCount());
    }

    @Test
    @DisplayName("Should handle concurrent operations safely")
    void shouldHandleConcurrentOperationsSafely() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);
        Customer customer = new Customer(TEST_FIRST_NAME, TEST_LAST_NAME);
        String accountNumber = bankService.openAccount(customer, null);
        
        bankService.deposit(accountNumber, Money.of(AMOUNT_1000_00));
        
        Runnable withdrawTask = () -> {
            try {
                bankService.withdraw(accountNumber, Money.of(AMOUNT_10_00));
            } catch (IllegalArgumentException e) {
                // Expected when insufficient funds - this is normal in concurrent testing
            }
        };
        
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(withdrawTask);
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        Money finalBalance = bankService.getBalance(TEST_ACCOUNT_NUMBER);
        assertTrue(finalBalance.isGreaterThanOrEqualTo(Money.ZERO));
        assertTrue(finalBalance.isLessThanOrEqualTo(Money.of(AMOUNT_1000_00)));
    }

    @Test
    @DisplayName("Should deposit money to existing account and record transaction")
    void shouldDepositMoneyToExistingAccountAndRecordTransaction() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);
        Customer customer = new Customer(TEST_FIRST_NAME, TEST_LAST_NAME);
        String accountNumber = bankService.openAccount(customer, null);
        Money depositAmount = Money.of(AMOUNT_100_50);

        Money newBalance = bankService.deposit(accountNumber, depositAmount);
        List< Transaction> transactions = bankService.getTransactionHistory(accountNumber, 10);

        assertEquals(depositAmount, newBalance);
        assertEquals(depositAmount, bankService.getBalance(accountNumber));
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        assertEquals(TransactionType.DEPOSIT, transactions.get(0).type());
        assertEquals(depositAmount, transactions.get(0).amount());
        assertEquals(newBalance, transactions.get(0).afterAmount());
    }

    @Test
    @DisplayName("Should throw exception when getting history for non-existent account")
    void shouldThrowExceptionWhenGettingHistoryForNonExistentAccount() {
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class,
                () -> bankService.getTransactionHistory(NON_EXISTENT_ACCOUNT_NUMBER, 10)
        );

        assertEquals(ACCOUNT_NOT_FOUND_ERROR_PREFIX + NON_EXISTENT_ACCOUNT_NUMBER, exception.getMessage());
    }
}
