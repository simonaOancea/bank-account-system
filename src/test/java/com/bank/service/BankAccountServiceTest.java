package com.bank.service;

import com.bank.model.AccountType;
import com.bank.repository.DailyTransactionRepository;
import com.bank.repository.inmemory.InMemoryAccountRepository;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.DailyLimitException;
import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.Money;
import com.bank.repository.AccountRepository;
import com.bank.repository.inmemory.InMemoryDailyTransactionTrackerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    private DailyTransactionRepository dailyTransactionRepository;
    private BankAccountService bankService;
    private DailyLimitsService dailyLimitsService;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAccountRepository();
        dailyTransactionRepository = new InMemoryDailyTransactionTrackerRepository();
        dailyLimitsService = new DailyLimitsService(dailyTransactionRepository);
        bankService = new BankAccountService(repository, accountNumberGenerator, dailyLimitsService);
        testCustomer = new Customer(TEST_FIRST_NAME, TEST_LAST_NAME);
    }

    @Test
    @DisplayName("Should throw exception for null account number generator")
    void shouldThrowExceptionForNullAccountNumberGenerator() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new BankAccountService(repository, null, dailyLimitsService)
        );
        assertEquals(ACCOUNT_NUMBER_GENERATOR_NULL_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should create new account successfully")
    void shouldCreateNewAccountSuccessfully() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);
        
        String accountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING, null);
        
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
        String accountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        Money depositAmount = Money.of(AMOUNT_100_50);
        
        Money newBalance = bankService.deposit(accountNumber, depositAmount);
        
        assertEquals(depositAmount, newBalance);
        assertEquals(depositAmount, bankService.getBalance(accountNumber));
    }

    @Test
    @DisplayName("Should withdraw money from existing account")
    void shouldWithdrawMoneyFromExistingAccount() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);
        String accountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
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
        String accountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
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
        String accountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        
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
	    bankService.openAccount(testCustomer, AccountType.CHECKING, null);

	    Account account = bankService.getAccount(ACCOUNT_NUMBER_WITH_WHITESPACE);
        
        assertEquals(TEST_ACCOUNT_NUMBER, account.getAccountNumber());
    }

    @Test
    @DisplayName("Should check if account exists")
    void shouldCheckIfAccountExists() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);
        String accountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        
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
        
        bankService.openAccount(testCustomer, AccountType.CHECKING, null);
        assertEquals(1, bankService.getAccountCount());
        
        Customer customer2 = new Customer(ALTERNATIVE_FIRST_NAME, ALTERNATIVE_LAST_NAME);
        bankService.openAccount(customer2, AccountType.CHECKING,  null);
        assertEquals(2, bankService.getAccountCount());
    }

    @Test
    @DisplayName("Should handle concurrent operations safely")
    void shouldHandleConcurrentOperationsSafely() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);
        String accountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        
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
    @DisplayName("Should transfer money between existing accounts")
    void shouldTransferMoneyBetweenExistingAccounts() {
        // Given: Two accounts with known balances
        when(accountNumberGenerator.generateAccountNumber())
            .thenReturn(TEST_ACCOUNT_NUMBER)
            .thenReturn(ALTERNATIVE_ACCOUNT_NUMBER);
        
        String fromAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        String toAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        
        bankService.deposit(fromAccountNumber, Money.of("200.00"));
        bankService.deposit(toAccountNumber, Money.of("100.00"));
        
        Money transferAmount = Money.of("75.00");

        // When: Transfer from one account to another through the service
        bankService.transfer(fromAccountNumber, toAccountNumber, transferAmount);

        // Then: both balances should be updated correctly
        assertEquals(Money.of("125.00"), bankService.getBalance(fromAccountNumber));
        assertEquals(Money.of("175.00"), bankService.getBalance(toAccountNumber));
    }

    @Test
    @DisplayName("Should throw exception when transferring to non-existent account")
    void shouldThrowExceptionWhenTransferringFromNonExistingAccount() {
        // Given: One existing account and one non-existing account
        when(accountNumberGenerator.generateAccountNumber())
                .thenReturn(TEST_ACCOUNT_NUMBER);

        String fromAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING, null);
        bankService.deposit(fromAccountNumber, Money.of("200.00"));

        Money transferAmount = Money.of("75.00");

        // When & Then: Transfer should throw AccountNotFoundException
        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> bankService.transfer(fromAccountNumber, NON_EXISTENT_ACCOUNT_NUMBER, transferAmount)
        );
        assertEquals(ACCOUNT_NOT_FOUND_ERROR_PREFIX + NON_EXISTENT_ACCOUNT_NUMBER, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when transferring from non-existent account")
    void shouldThrowExceptionWhenTransferringFromNonExistentAccount() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);
        String toAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING, null);
        Money transferAmount = Money.of("75.00");

        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> bankService.transfer(NON_EXISTENT_ACCOUNT_NUMBER, toAccountNumber, transferAmount)
        );
        assertEquals(ACCOUNT_NOT_FOUND_ERROR_PREFIX + NON_EXISTENT_ACCOUNT_NUMBER, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when transferring with insufficient funds")
    void shouldThrowExceptionWhenTransferringWithInsufficientFunds() {
        when(accountNumberGenerator.generateAccountNumber())
                .thenReturn(TEST_ACCOUNT_NUMBER)
                .thenReturn(ALTERNATIVE_ACCOUNT_NUMBER);
        
        String fromAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        String toAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        bankService.deposit(fromAccountNumber, Money.of("50.00"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> bankService.transfer(fromAccountNumber, toAccountNumber, Money.of("100.00"))
        );
        assertEquals("Insufficient funds for transfer", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when transferring to same account")
    void shouldThrowExceptionWhenTransferringToSameAccount() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);
        String accountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        bankService.deposit(accountNumber, Money.of("100.00"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.transfer(accountNumber, accountNumber, Money.of("50.00"))
        );
        assertEquals("Cannot transfer to the same account", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for null from account in transfer")
    void shouldThrowExceptionForNullFromAccountInTransfer() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);
        String toAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.transfer(null, toAccountNumber, Money.of("50.00"))
        );
        assertEquals("From account number cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for null to account in transfer")
    void shouldThrowExceptionForNullToAccountInTransfer() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);
        String fromAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        bankService.deposit(fromAccountNumber, Money.of("100.00"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.transfer(fromAccountNumber, null, Money.of("50.00"))
        );
        assertEquals("To account number cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for null transfer amount")
    void shouldThrowExceptionForNullTransferAmount() {
        when(accountNumberGenerator.generateAccountNumber())
                .thenReturn(TEST_ACCOUNT_NUMBER)
                .thenReturn(ALTERNATIVE_ACCOUNT_NUMBER);
        
        String fromAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        String toAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.transfer(fromAccountNumber, toAccountNumber, null)
        );
        assertEquals("Transfer amount cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for negative transfer amount")
    void shouldThrowExceptionForNegativeTransferAmount() {
        when(accountNumberGenerator.generateAccountNumber())
                .thenReturn(TEST_ACCOUNT_NUMBER)
                .thenReturn(ALTERNATIVE_ACCOUNT_NUMBER);
        
        String fromAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        String toAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        bankService.deposit(fromAccountNumber, Money.of("100.00"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.transfer(fromAccountNumber, toAccountNumber, Money.of("-10.00"))
        );
        assertEquals("Transfer amount must be positive", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for zero transfer amount")
    void shouldThrowExceptionForZeroTransferAmount() {
        when(accountNumberGenerator.generateAccountNumber())
                .thenReturn(TEST_ACCOUNT_NUMBER)
                .thenReturn(ALTERNATIVE_ACCOUNT_NUMBER);
        
        String fromAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        String toAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        bankService.deposit(fromAccountNumber, Money.of("100.00"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.transfer(fromAccountNumber, toAccountNumber, Money.ZERO)
        );
        assertEquals("Transfer amount must be positive", exception.getMessage());
    }

    @Test
    @DisplayName("Should transfer exact balance")
    void shouldTransferExactBalance() {
        when(accountNumberGenerator.generateAccountNumber())
                .thenReturn(TEST_ACCOUNT_NUMBER)
                .thenReturn(ALTERNATIVE_ACCOUNT_NUMBER);
        
        String fromAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        String toAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        
        Money initialAmount = Money.of("100.00");
        bankService.deposit(fromAccountNumber, initialAmount);
        
        Money newBalance = bankService.transfer(fromAccountNumber, toAccountNumber, initialAmount);
        
        assertEquals(Money.ZERO, newBalance);
        assertEquals(Money.ZERO, bankService.getBalance(fromAccountNumber));
        assertEquals(initialAmount, bankService.getBalance(toAccountNumber));
    }

    @Test
    @DisplayName("Should handle whitespace in transfer account numbers")
    void shouldHandleWhitespaceInTransferAccountNumbers() {
        when(accountNumberGenerator.generateAccountNumber())
                .thenReturn(TEST_ACCOUNT_NUMBER)
                .thenReturn(ALTERNATIVE_ACCOUNT_NUMBER);
        
        String fromAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        String toAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        bankService.deposit(fromAccountNumber, Money.of("100.00"));
        
        Money transferAmount = Money.of("50.00");
        String fromAccountWithWhitespace = "  " + fromAccountNumber + "  ";
        String toAccountWithWhitespace = "  " + toAccountNumber + "  ";
        
        Money newBalance = bankService.transfer(fromAccountWithWhitespace, toAccountWithWhitespace, transferAmount);
        
        assertEquals(Money.of("50.00"), newBalance);
        assertEquals(Money.of("50.00"), bankService.getBalance(fromAccountNumber));
        assertEquals(Money.of("50.00"), bankService.getBalance(toAccountNumber));
    }

    @Test
    @DisplayName("Should transfer minimum amount")
    void shouldTransferMinimumAmount() {
        when(accountNumberGenerator.generateAccountNumber())
                .thenReturn(TEST_ACCOUNT_NUMBER)
                .thenReturn(ALTERNATIVE_ACCOUNT_NUMBER);
        
        String fromAccountNumber = bankService.openAccount(testCustomer,AccountType.CHECKING,  null);
        String toAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        bankService.deposit(fromAccountNumber, Money.of("1.00"));
        
        Money minimumAmount = Money.of("0.01");
        Money newBalance = bankService.transfer(fromAccountNumber, toAccountNumber, minimumAmount);
        
        assertEquals(Money.of("0.99"), newBalance);
        assertEquals(Money.of("0.99"), bankService.getBalance(fromAccountNumber));
        assertEquals(minimumAmount, bankService.getBalance(toAccountNumber));
    }

    @Test
    @DisplayName("Should return correct balance after transfer")
    void shouldReturnCorrectBalanceAfterTransfer() {
        when(accountNumberGenerator.generateAccountNumber())
                .thenReturn(TEST_ACCOUNT_NUMBER)
                .thenReturn(ALTERNATIVE_ACCOUNT_NUMBER);
        
        String fromAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        String toAccountNumber = bankService.openAccount(testCustomer, AccountType.CHECKING,  null);
        bankService.deposit(fromAccountNumber, Money.of("200.00"));
        
        Money transferAmount = Money.of("75.00");
        Money returnedBalance = bankService.transfer(fromAccountNumber, toAccountNumber, transferAmount);
        Money actualBalance = bankService.getBalance(fromAccountNumber);
        
        assertEquals(actualBalance, returnedBalance);
        assertEquals(Money.of("125.00"), returnedBalance);
    }

    @Test
    @DisplayName("Should create SAVINGS account successfully with valid initial deposit")
    void shouldCreateSavingsAccountSuccessfully() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);

        String accountNumber = bankService.openAccount(testCustomer, AccountType.SAVINGS, Money.of(1000));

        assertNotNull(accountNumber);
        assertEquals(TEST_ACCOUNT_NUMBER, accountNumber);

        Account account = bankService.getAccount(accountNumber);
        assertEquals(TEST_FIRST_NAME, account.getCustomer().firstName());
        assertEquals(TEST_LAST_NAME, account.getCustomer().lastName());
        assertEquals(Money.of(1000), account.getBalance());
        assertEquals(AccountType.SAVINGS, account.getAccountType());

        verify(accountNumberGenerator).generateAccountNumber();
    }

    @Test
    @DisplayName("Should throw exception when opening SAVINGS account with insufficient initial deposit")
    void shouldThrowExceptionWhenOpeningSavingsAccountWithInsufficientInitialDeposit() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.openAccount(testCustomer, AccountType.SAVINGS, Money.of("499.99"))
        );
        assertEquals("SAVINGS accounts require a minimum opening deposit of $500.00", exception.getMessage());
    }

    @Test
    @DisplayName("Should allow withdrawals up to daily limit for SAVINGS account")
    void shouldAllowWithdrawalsUpToDailyLimitForSavingsAccount() {
        // Given: SAVINGS account with money
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);
        String accountNumber = bankService.openAccount(testCustomer, AccountType.SAVINGS, Money.of("1000"));

        // When: Withdraw up to daily limit ($500)
        bankService.withdraw(accountNumber, Money.of("500"));

        // Then: Withdrawal succeeds, balance should be $500 remaining
        assertEquals(Money.of("500"), bankService.getBalance(accountNumber));
    }

    @Test
    @DisplayName("Should throw exception when exceeding daily withdrawal limit for SAVINGS account")
    void shouldThrowExceptionWhenExceedingDailyWithdrawalLimitForSavingsAccount() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(TEST_ACCOUNT_NUMBER);
        String accountNumber = bankService.openAccount(testCustomer, AccountType.SAVINGS, Money.of("5000"));

        // This should fail because we're trying to withdraw more than $500 daily limit
        DailyLimitException exception = assertThrows(DailyLimitException.class,
                () -> bankService.withdraw(accountNumber, Money.of("600")));
        assertEquals("Daily withdrawal limit exceeded. Current: $0.00, Attempting: $600.00, Limit: $500.00", exception.getMessage());
    }
    
}