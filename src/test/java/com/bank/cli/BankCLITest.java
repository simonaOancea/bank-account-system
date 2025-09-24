package com.bank.cli;

import com.bank.repository.DailyTransactionRepository;
import com.bank.repository.inmemory.InMemoryAccountRepository;
import com.bank.repository.AccountRepository;
import com.bank.repository.inmemory.InMemoryDailyTransactionTrackerRepository;
import com.bank.service.BankAccountService;
import com.bank.service.DailyLimitsService;
import com.bank.service.SimpleAccountNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class BankCLITest {

    private static final String NEW_ACCOUNT_COMMAND_JOHN_DOE = "NewAccount John Doe";
    private static final String NEW_ACCOUNT_COMMAND_ALICE_SMITH = "NewAccount Alice Smith";
    private static final String NEW_ACCOUNT_COMMAND_JOHN_ONLY = "NewAccount John";
    private static final String CASE_INSENSITIVE_COMMAND = "newaccount john doe";
    private static final String QUIT_COMMAND = "Quit";
    private static final String INVALID_COMMAND = "InvalidCommand";
    private static final String WHITESPACE_ONLY_INPUT = "   ";
    
    private static final String AMOUNT_100_50 = "100.50";
    private static final String AMOUNT_200_00 = "200.00";
    private static final String AMOUNT_75_25 = "75.25";
    private static final String AMOUNT_150_75 = "150.75";
    private static final String AMOUNT_100_00 = "100.00";
    private static final String AMOUNT_500_00 = "500.00";
    private static final String AMOUNT_150_25 = "150.25";
    private static final String FORMATTED_0_00 = "$0.00";
    private static final String FORMATTED_100_50 = "$100.50";
    private static final String FORMATTED_75_25 = "$75.25";
    private static final String FORMATTED_150_75 = "$150.75";
    private static final String FORMATTED_500_00 = "$500.00";
    private static final String FORMATTED_150_25 = "$150.25";
    private static final String FORMATTED_349_75 = "$349.75";
    
    private static final String NON_EXISTENT_ACCOUNT_NUMBER = "99999";
    private static final String INVALID_AMOUNT = "invalid_amount";
    private static final String TEST_ACCOUNT_NUMBER = "12345";
    private static final String DEFAULT_FALLBACK_ACCOUNT = "1000001";
    
    private static final String CHECKING_ACCOUNT_CREATED_SUCCESSFULLY = "Checking account created successfully";
    private static final String ACCOUNT_NUMBER_LABEL = "Account number:";
    private static final String INVALID_COMMAND_MESSAGE = "Invalid command";
    private static final String ACCOUNT_NOT_FOUND_MESSAGE = "Account not found";
    private static final String INSUFFICIENT_FUNDS_MESSAGE = "Insufficient funds";
    private static final String NEW_ACCOUNT_USAGE = "Usage: NewAccount [First Name] [Last Name]";
    private static final String DEPOSIT_USAGE = "Usage: Deposit [Amount] [Account Number]";

	private BankCLI cli;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        AccountRepository repository = new InMemoryAccountRepository();
        DailyTransactionRepository dailyTransactionRepository = new InMemoryDailyTransactionTrackerRepository();
        DailyLimitsService dailyLimitsService = new DailyLimitsService(dailyTransactionRepository);
	    BankAccountService bankService = new BankAccountService(repository, new SimpleAccountNumberGenerator(), dailyLimitsService);
        cli = new BankCLI(bankService);
        
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should handle NewAccount command")
    void shouldHandleNewAccountCommand() {
        cli.processCommand(NEW_ACCOUNT_COMMAND_JOHN_DOE);
        
        String output = outputStream.toString();
        assertTrue(output.contains(CHECKING_ACCOUNT_CREATED_SUCCESSFULLY));
        assertTrue(output.contains(ACCOUNT_NUMBER_LABEL));
    }

    @Test
    @DisplayName("Should handle Deposit command")
    void shouldHandleDepositCommand() {
        cli.processCommand(NEW_ACCOUNT_COMMAND_JOHN_DOE);
        outputStream.reset();
        
        String accountNumber = getAccountNumberFromLastOutput();
        cli.processCommand("Deposit " + AMOUNT_100_50 + " " + accountNumber);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Deposited " + FORMATTED_100_50));
        assertTrue(output.contains("to account " + accountNumber));
    }

    @Test
    @DisplayName("Should handle Withdraw command")
    void shouldHandleWithdrawCommand() {
        cli.processCommand(NEW_ACCOUNT_COMMAND_JOHN_DOE);
        String accountNumber = getAccountNumberFromLastOutput();
        outputStream.reset();
        
        cli.processCommand("Deposit " + AMOUNT_200_00 + " " + accountNumber);
        outputStream.reset();
        
        cli.processCommand("Withdraw " + AMOUNT_75_25 + " " + accountNumber);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Withdrew " + FORMATTED_75_25));
        assertTrue(output.contains("from account " + accountNumber));
    }

    @Test
    @DisplayName("Should handle Balance command")
    void shouldHandleBalanceCommand() {
        cli.processCommand(NEW_ACCOUNT_COMMAND_JOHN_DOE);
        String accountNumber = getAccountNumberFromLastOutput();
        outputStream.reset();
        
        cli.processCommand("Deposit " + AMOUNT_150_75 + " " + accountNumber);
        outputStream.reset();
        
        cli.processCommand("Balance " + accountNumber);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Account " + accountNumber + " balance: " + FORMATTED_150_75));
    }

    @Test
    @DisplayName("Should handle Quit command")
    void shouldHandleQuitCommand() {
        cli.processCommand(QUIT_COMMAND);
        
        assertFalse(cli.isRunning());
    }

    @Test
    @DisplayName("Should handle invalid command")
    void shouldHandleInvalidCommand() {
        cli.processCommand(INVALID_COMMAND);
        
        String output = outputStream.toString();
        assertTrue(output.contains(INVALID_COMMAND_MESSAGE));
    }

    @Test
    @DisplayName("Should handle NewAccount with invalid arguments")
    void shouldHandleNewAccountWithInvalidArguments() {
        cli.processCommand(NEW_ACCOUNT_COMMAND_JOHN_ONLY);
        
        String output = outputStream.toString();
        assertTrue(output.contains(NEW_ACCOUNT_USAGE));
    }

    @Test
    @DisplayName("Should handle Deposit with invalid arguments")
    void shouldHandleDepositWithInvalidArguments() {
        cli.processCommand("Deposit " + INVALID_AMOUNT + " " + TEST_ACCOUNT_NUMBER);
        
        String output = outputStream.toString();
        assertTrue(output.contains(DEPOSIT_USAGE));
    }

    @Test
    @DisplayName("Should handle Deposit to non-existent account")
    void shouldHandleDepositToNonExistentAccount() {
        cli.processCommand("Deposit " + AMOUNT_100_00 + " " + NON_EXISTENT_ACCOUNT_NUMBER);
        
        String output = outputStream.toString();
        assertTrue(output.contains(ACCOUNT_NOT_FOUND_MESSAGE));
    }

    @Test
    @DisplayName("Should handle Withdraw with insufficient funds")
    void shouldHandleWithdrawWithInsufficientFunds() {
        cli.processCommand(NEW_ACCOUNT_COMMAND_JOHN_DOE);
        String accountNumber = getAccountNumberFromLastOutput();
        outputStream.reset();
        
        cli.processCommand("Withdraw " + AMOUNT_100_00 + " " + accountNumber);
        
        String output = outputStream.toString();
        assertTrue(output.contains(INSUFFICIENT_FUNDS_MESSAGE));
    }

    @Test
    @DisplayName("Should handle Balance for non-existent account")
    void shouldHandleBalanceForNonExistentAccount() {
        cli.processCommand("Balance " + NON_EXISTENT_ACCOUNT_NUMBER);
        
        String output = outputStream.toString();
        assertTrue(output.contains(ACCOUNT_NOT_FOUND_MESSAGE));
    }

    @Test
    @DisplayName("Should handle empty input")
    void shouldHandleEmptyInput() {
        cli.processCommand("");
        
        String output = outputStream.toString();
        assertTrue(output.contains(INVALID_COMMAND_MESSAGE));
    }

    @Test
    @DisplayName("Should handle whitespace-only input")
    void shouldHandleWhitespaceOnlyInput() {
        cli.processCommand(WHITESPACE_ONLY_INPUT);
        
        String output = outputStream.toString();
        assertTrue(output.contains(INVALID_COMMAND_MESSAGE));
    }

    @Test
    @DisplayName("Should handle case-insensitive commands")
    void shouldHandleCaseInsensitiveCommands() {
        cli.processCommand(CASE_INSENSITIVE_COMMAND);
        
        String output = outputStream.toString();
        assertTrue(output.contains(CHECKING_ACCOUNT_CREATED_SUCCESSFULLY));
    }

    @Test
    @DisplayName("Should handle full transaction workflow")
    void shouldHandleFullTransactionWorkflow() {
        cli.processCommand(NEW_ACCOUNT_COMMAND_ALICE_SMITH);
        String accountNumber = getAccountNumberFromLastOutput();
        outputStream.reset();

        cli.processCommand("Balance " + accountNumber);
        assertTrue(outputStream.toString().contains(FORMATTED_0_00));
        outputStream.reset();

        cli.processCommand("Deposit " + AMOUNT_500_00 + " " + accountNumber);
        assertTrue(outputStream.toString().contains("Deposited " + FORMATTED_500_00));
        outputStream.reset();

        cli.processCommand("Balance " + accountNumber);
        assertTrue(outputStream.toString().contains(FORMATTED_500_00));
        outputStream.reset();

        cli.processCommand("Withdraw " + AMOUNT_150_25 + " " + accountNumber);
        assertTrue(outputStream.toString().contains("Withdrew " + FORMATTED_150_25));
        outputStream.reset();

        cli.processCommand("Balance " + accountNumber);
        assertTrue(outputStream.toString().contains(FORMATTED_349_75));
        
        tearDown();
    }

    private String getAccountNumberFromLastOutput() {
        String output = outputStream.toString();
        String[] lines = output.split("\n");
        for (String line : lines) {
            if (line.contains(ACCOUNT_NUMBER_LABEL)) {
                return line.substring(line.lastIndexOf(" ") + 1).trim();
            }
        }
        return DEFAULT_FALLBACK_ACCOUNT;
    }
}