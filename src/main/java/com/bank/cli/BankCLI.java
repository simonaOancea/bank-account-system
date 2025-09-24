package com.bank.cli;

import com.bank.exception.AccountNotFoundException;
import com.bank.model.Account;
import com.bank.model.AccountType;
import com.bank.model.Customer;
import com.bank.model.Money;
import com.bank.service.BankAccountService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class BankCLI {

    private static final String WELCOME_MESSAGE = "Welcome to Bank Account System";
    private static final String AVAILABLE_COMMANDS = "Available commands:";
    private static final String NEW_ACCOUNT_HELP = "  NewAccount [First Name] [Last Name] - Create a new checking account";
    private static final String NEW_SAVINGS_ACCOUNT_HELP = "  NewSavingsAccount [First Name] [Last Name] [Initial Deposit] - Create a new savings account";
    private static final String DEPOSIT_HELP = "  Deposit [Amount] [Account Number] - Deposit money";
    private static final String WITHDRAW_HELP = "  Withdraw [Amount] [Account Number] - Withdraw money";
    private static final String BALANCE_HELP = "  Balance [Account Number] - Check balance";
    private static final String TRANSFER_HELP = "  Transfer [From Account] [To Account] [Amount] - Transfer money between accounts";
    private static final String QUIT_HELP = "  Quit - Exit the program";
    private static final String GOODBYE_MESSAGE = "Thank you for using Bank Account System!";

    private static final String PROMPT = "> ";
    private static final String INVALID_COMMAND = "Invalid command. Type a valid command or 'Quit' to exit.";
    private static final String ERROR_PREFIX = "Error: ";
    private static final String ERROR_READING_INPUT = "Error reading input: ";
    private static final String UNEXPECTED_ERROR = "Unexpected error: ";

    private static final String NEW_ACCOUNT_USAGE = "Usage: NewAccount [First Name] [Last Name]";
    private static final String NEW_SAVINGS_ACCOUNT_USAGE = "Usage: NewSavingsAccount [First Name] [Last Name] [Initial Deposit]";
    private static final String DEPOSIT_USAGE = "Usage: Deposit [Amount] [Account Number]";
    private static final String WITHDRAW_USAGE = "Usage: Withdraw [Amount] [Account Number]";
    private static final String BALANCE_USAGE = "Usage: Balance [Account Number]";
    private static final String TRANSFER_USAGE = "Usage: Transfer [Account Number] [Account Number] [Amount]";

    private static final String ACCOUNT_CREATED = "Checking account created successfully. Account number: ";
    private static final String SAVINGS_ACCOUNT_CREATED = "Savings account created successfully. Account number: ";
    private static final String DEPOSITED_FORMAT = "Deposited %s to account %s. New balance: %s%n";
    private static final String WITHDREW_FORMAT = "Withdrew %s from account %s. New balance: %s%n";
    private static final String BALANCE_FORMAT = "Account %s balance: %s%n";
    private static final String TRANSFER_FORMAT = "Transferred %s from account %s to account %s. From account new balance: %s%n";

    private static final String INVALID_AMOUNT_ERROR = "Error: Invalid amount. Please enter a positive number.";
    private static final String SAVINGS_MIN_DEPOSIT_AMOUNT = "500.00";
    private static final String SAVINGS_MIN_DEPOSIT_ERROR = "Minimum initial deposit for SAVINGS account is $" + SAVINGS_MIN_DEPOSIT_AMOUNT;

    private final BankAccountService bankService;
    private final CommandParser commandParser;
    private final BufferedReader reader;
    private boolean running;

    public BankCLI(BankAccountService bankService) {
        this.bankService = bankService;
        this.commandParser = new CommandParser();
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.running = false;
    }

    public void start() {
        running = true;
        System.out.println(WELCOME_MESSAGE);
        System.out.println(AVAILABLE_COMMANDS);
        System.out.println(NEW_ACCOUNT_HELP);
        System.out.println(NEW_SAVINGS_ACCOUNT_HELP);
        System.out.println(DEPOSIT_HELP);
        System.out.println(WITHDRAW_HELP);
        System.out.println(BALANCE_HELP);
        System.out.println(TRANSFER_HELP);
        System.out.println(QUIT_HELP);
        System.out.println();

        while (running) {
            try {
                System.out.print(PROMPT);
                String input = reader.readLine();
                
                if (Objects.isNull(input)) {
                    break;
                }
                
                processCommand(input);
            } catch (IOException e) {
                System.err.println(ERROR_READING_INPUT + e.getMessage());
                break;
            } catch (Exception e) {
                System.err.println(UNEXPECTED_ERROR + e.getMessage());
            }
        }
        
        System.out.println(GOODBYE_MESSAGE);
    }

    public void processCommand(String input) {
        CommandParser.ParsedCommand parsedCommand = commandParser.parseCommand(input);
        
        if (Objects.isNull(parsedCommand)) {
            System.out.println(INVALID_COMMAND);
            return;
        }

        Command command = parsedCommand.command();
        List<String> arguments = parsedCommand.arguments();

        try {
            switch (command) {
                case NEW_ACCOUNT -> handleNewAccount(arguments);
                case NEW_SAVINGS_ACCOUNT -> handleNewSavingsAccount(arguments);
                case DEPOSIT -> handleDeposit(arguments);
                case WITHDRAW -> handleWithdraw(arguments);
                case BALANCE -> handleBalance(arguments);
                case TRANSFER -> handleTransfer(arguments);
                case QUIT -> handleQuit();
            }
        } catch (AccountNotFoundException | IllegalArgumentException e) {
            System.out.println(ERROR_PREFIX + e.getMessage());
        }
    }

    private void handleNewAccount(List<String> arguments) {
        if (!commandParser.isValidNewAccountCommand(arguments)) {
            System.out.println(NEW_ACCOUNT_USAGE);
            return;
        }

        String firstName = arguments.get(0);
        String lastName = arguments.get(1);
        Customer customer = new Customer(firstName, lastName);
        
        String accountNumber = bankService.openAccount(customer,  AccountType.CHECKING,  null);
        System.out.println(ACCOUNT_CREATED + accountNumber);
    }

    private void handleNewSavingsAccount(List<String> arguments) {
        if (!commandParser.isValidNewSavingsAccountCommand(arguments)) {
            System.out.println(NEW_SAVINGS_ACCOUNT_USAGE);
            return;
        }

        String firstName = arguments.get(0);
        String lastName = arguments.get(1);
        String initialDepositStr = arguments.get(2);

        BigDecimal initialDeposit = commandParser.parseAmount(initialDepositStr);
        if (initialDeposit == null) {
            System.out.println(INVALID_AMOUNT_ERROR);
            return;
        }

        Customer customer = new Customer(firstName, lastName);
        String accountNumber = bankService.openAccount(customer, AccountType.SAVINGS, Money.of(initialDeposit));
        System.out.println(SAVINGS_ACCOUNT_CREATED + accountNumber);
    }

    private void handleDeposit(List<String> arguments) {
        if (!commandParser.isValidDepositCommand(arguments)) {
            System.out.println(DEPOSIT_USAGE);
            return;
        }

        BigDecimal amount = commandParser.parseAmount(arguments.get(0));
        if (Objects.isNull(amount)) {
            System.out.println(INVALID_AMOUNT_ERROR);
            return;
        }

        String accountNumber = arguments.get(1);
        Money money = Money.of(amount);
        
        Money newBalance = bankService.deposit(accountNumber, money);
        System.out.printf(DEPOSITED_FORMAT, 
                         money.toFormattedString(), accountNumber, newBalance.toFormattedString());
    }

    private void handleWithdraw(List<String> arguments) {
        if (!commandParser.isValidWithdrawCommand(arguments)) {
            System.out.println(WITHDRAW_USAGE);
            return;
        }

        BigDecimal amount = commandParser.parseAmount(arguments.get(0));
        if (Objects.isNull(amount)) {
            System.out.println(INVALID_AMOUNT_ERROR);
            return;
        }

        String accountNumber = arguments.get(1);
        Money money = Money.of(amount);
        
        Money newBalance = bankService.withdraw(accountNumber, money);
        System.out.printf(WITHDREW_FORMAT, 
                         money.toFormattedString(), accountNumber, newBalance.toFormattedString());
    }

    private void handleBalance(List<String> arguments) {
        if (!commandParser.isValidBalanceCommand(arguments)) {
            System.out.println(BALANCE_USAGE);
            return;
        }

        String accountNumber = arguments.get(0);
        
        Money balance = bankService.getBalance(accountNumber);
        System.out.printf(BALANCE_FORMAT, accountNumber, balance.toFormattedString());
    }

    private void handleTransfer(List<String> arguments) {
        if (!commandParser.isValidTransferCommand(arguments)) {
            System.out.println(TRANSFER_USAGE);
            return;
        }

        String accountFrom = arguments.get(0);
        String accountTo = arguments.get(1);
        BigDecimal amount = commandParser.parseAmount(arguments.get(2));
        Money money = Money.of(amount);

        Money currentBalance = bankService.transfer(accountFrom, accountTo, money);
        System.out.printf(TRANSFER_FORMAT, money.toFormattedString(), accountFrom, accountTo, currentBalance.toFormattedString());
    }

    private void handleQuit() {
        running = false;
    }

    public void stop() {
        running = false;
    }

    boolean isRunning() {
        return running;
    }
}