package com.bank.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {

    private static final String NEW_ACCOUNT_COMMAND_JOHN_DOE = "NewAccount John Doe";
    private static final String DEPOSIT_COMMAND_100_50 = "Deposit 100.50 12345";
    private static final String WITHDRAW_COMMAND_50_25 = "Withdraw 50.25 67890";
    private static final String BALANCE_COMMAND = "Balance 12345";
    private static final String QUIT_COMMAND = "Quit";
    private static final String CASE_INSENSITIVE_COMMAND = "newaccount John Doe";
    private static final String WHITESPACE_COMMAND = "  NewAccount   John   Doe  ";
    private static final String WHITESPACE_ONLY_INPUT = "   ";
    private static final String UNKNOWN_COMMAND = "UnknownCommand arg1 arg2";
    
    private static final String FIRST_NAME_JOHN = "John";
    private static final String LAST_NAME_DOE = "Doe";
    private static final String SUFFIX_JR = "Jr";
    private static final String EXTRA_ARG = "extra";
    
    private static final String ACCOUNT_NUMBER_12345 = "12345";
    private static final String ACCOUNT_NUMBER_67890 = "67890";
    
    private static final String AMOUNT_100_50 = "100.50";
    private static final String AMOUNT_50_25 = "50.25";
    private static final String AMOUNT_100_00 = "100.00";
    private static final String AMOUNT_0_01 = "0.01";
    private static final String AMOUNT_999_99 = "999.99";
    private static final String AMOUNT_0_00 = "0.00";
    private static final String AMOUNT_100 = "100";
    private static final String AMOUNT_NEGATIVE_100_00 = "-100.00";
    private static final String AMOUNT_ZERO = "0";
    
    private static final String INVALID_AMOUNT = "invalid";

    private CommandParser commandParser;

    @BeforeEach
    void setUp() {
        commandParser = new CommandParser();
    }

    @Test
    @DisplayName("Should parse NewAccount command correctly")
    void shouldParseNewAccountCommandCorrectly() {
        CommandParser.ParsedCommand result = commandParser.parseCommand(NEW_ACCOUNT_COMMAND_JOHN_DOE);
        
        assertNotNull(result);
        assertEquals(Command.NEW_ACCOUNT, result.command());
        assertEquals(List.of(FIRST_NAME_JOHN, LAST_NAME_DOE), result.arguments());
    }

    @Test
    @DisplayName("Should parse Deposit command correctly")
    void shouldParseDepositCommandCorrectly() {
        CommandParser.ParsedCommand result = commandParser.parseCommand(DEPOSIT_COMMAND_100_50);
        
        assertNotNull(result);
        assertEquals(Command.DEPOSIT, result.command());
        assertEquals(List.of(AMOUNT_100_50, ACCOUNT_NUMBER_12345), result.arguments());
    }

    @Test
    @DisplayName("Should parse Withdraw command correctly")
    void shouldParseWithdrawCommandCorrectly() {
        CommandParser.ParsedCommand result = commandParser.parseCommand(WITHDRAW_COMMAND_50_25);
        
        assertNotNull(result);
        assertEquals(Command.WITHDRAW, result.command());
        assertEquals(List.of(AMOUNT_50_25, ACCOUNT_NUMBER_67890), result.arguments());
    }

    @Test
    @DisplayName("Should parse Balance command correctly")
    void shouldParseBalanceCommandCorrectly() {
        CommandParser.ParsedCommand result = commandParser.parseCommand(BALANCE_COMMAND);
        
        assertNotNull(result);
        assertEquals(Command.BALANCE, result.command());
        assertEquals(List.of(ACCOUNT_NUMBER_12345), result.arguments());
    }

    @Test
    @DisplayName("Should parse Quit command correctly")
    void shouldParseQuitCommandCorrectly() {
        CommandParser.ParsedCommand result = commandParser.parseCommand(QUIT_COMMAND);
        
        assertNotNull(result);
        assertEquals(Command.QUIT, result.command());
        assertTrue(result.arguments().isEmpty());
    }

    @Test
    @DisplayName("Should handle case insensitive commands")
    void shouldHandleCaseInsensitiveCommands() {
        CommandParser.ParsedCommand result = commandParser.parseCommand(CASE_INSENSITIVE_COMMAND);
        
        assertNotNull(result);
        assertEquals(Command.NEW_ACCOUNT, result.command());
    }

    @Test
    @DisplayName("Should handle extra whitespace")
    void shouldHandleExtraWhitespace() {
        CommandParser.ParsedCommand result = commandParser.parseCommand(WHITESPACE_COMMAND);
        
        assertNotNull(result);
        assertEquals(Command.NEW_ACCOUNT, result.command());
        assertEquals(List.of(FIRST_NAME_JOHN, LAST_NAME_DOE), result.arguments());
    }

    @Test
    @DisplayName("Should return null for null input")
    void shouldReturnNullForNullInput() {
        CommandParser.ParsedCommand result = commandParser.parseCommand(null);
        
        assertNull(result);
    }

    @Test
    @DisplayName("Should return null for empty input")
    void shouldReturnNullForEmptyInput() {
        CommandParser.ParsedCommand result = commandParser.parseCommand("");
        
        assertNull(result);
    }

    @Test
    @DisplayName("Should return null for whitespace-only input")
    void shouldReturnNullForWhitespaceOnlyInput() {
        CommandParser.ParsedCommand result = commandParser.parseCommand(WHITESPACE_ONLY_INPUT);
        
        assertNull(result);
    }

    @Test
    @DisplayName("Should return null for unknown command")
    void shouldReturnNullForUnknownCommand() {
        CommandParser.ParsedCommand result = commandParser.parseCommand(UNKNOWN_COMMAND);
        
        assertNull(result);
    }

    @Test
    @DisplayName("Should validate NewAccount command with correct arguments")
    void shouldValidateNewAccountCommandWithCorrectArguments() {
        List<String> validArgs = List.of(FIRST_NAME_JOHN, LAST_NAME_DOE);
        
        assertTrue(commandParser.isValidNewAccountCommand(validArgs));
    }

    @Test
    @DisplayName("Should invalidate NewAccount command with incorrect argument count")
    void shouldInvalidateNewAccountCommandWithIncorrectArgumentCount() {
        List<String> tooFewArgs = List.of(FIRST_NAME_JOHN);
        List<String> tooManyArgs = List.of(FIRST_NAME_JOHN, LAST_NAME_DOE, SUFFIX_JR);
        
        assertFalse(commandParser.isValidNewAccountCommand(tooFewArgs));
        assertFalse(commandParser.isValidNewAccountCommand(tooManyArgs));
    }

    @Test
    @DisplayName("Should invalidate NewAccount command with empty arguments")
    void shouldInvalidateNewAccountCommandWithEmptyArguments() {
        List<String> emptyFirstName = List.of("", LAST_NAME_DOE);
        List<String> emptyLastName = List.of(FIRST_NAME_JOHN, "");
        
        assertFalse(commandParser.isValidNewAccountCommand(emptyFirstName));
        assertFalse(commandParser.isValidNewAccountCommand(emptyLastName));
    }

    @Test
    @DisplayName("Should validate Deposit command with correct arguments")
    void shouldValidateDepositCommandWithCorrectArguments() {
        List<String> validArgs = List.of(AMOUNT_100_50, ACCOUNT_NUMBER_12345);
        
        assertTrue(commandParser.isValidDepositCommand(validArgs));
    }

    @Test
    @DisplayName("Should invalidate Deposit command with invalid amount")
    void shouldInvalidateDepositCommandWithInvalidAmount() {
        List<String> invalidAmount = List.of(INVALID_AMOUNT, ACCOUNT_NUMBER_12345);
        List<String> negativeAmount = List.of(AMOUNT_NEGATIVE_100_00, ACCOUNT_NUMBER_12345);
        List<String> zeroAmount = List.of(AMOUNT_0_00, ACCOUNT_NUMBER_12345);
        
        assertFalse(commandParser.isValidDepositCommand(invalidAmount));
        assertFalse(commandParser.isValidDepositCommand(negativeAmount));
        assertFalse(commandParser.isValidDepositCommand(zeroAmount));
    }

    @Test
    @DisplayName("Should validate Withdraw command with correct arguments")
    void shouldValidateWithdrawCommandWithCorrectArguments() {
        List<String> validArgs = List.of(AMOUNT_50_25, ACCOUNT_NUMBER_67890);
        
        assertTrue(commandParser.isValidWithdrawCommand(validArgs));
    }

    @Test
    @DisplayName("Should validate Balance command with correct arguments")
    void shouldValidateBalanceCommandWithCorrectArguments() {
        List<String> validArgs = List.of("12345");
        
        assertTrue(commandParser.isValidBalanceCommand(validArgs));
    }

    @Test
    @DisplayName("Should invalidate Balance command with incorrect argument count")
    void shouldInvalidateBalanceCommandWithIncorrectArgumentCount() {
        List<String> noArgs = List.of();
        List<String> tooManyArgs = List.of(ACCOUNT_NUMBER_12345, EXTRA_ARG);
        
        assertFalse(commandParser.isValidBalanceCommand(noArgs));
        assertFalse(commandParser.isValidBalanceCommand(tooManyArgs));
    }

    @Test
    @DisplayName("Should parse valid decimal amounts")
    void shouldParseValidDecimalAmounts() {
        assertEquals(new BigDecimal(AMOUNT_100_50), commandParser.parseAmount(AMOUNT_100_50));
        assertEquals(new BigDecimal(AMOUNT_0_01), commandParser.parseAmount(AMOUNT_0_01));
        assertEquals(new BigDecimal(AMOUNT_999_99), commandParser.parseAmount(AMOUNT_999_99));
    }

    @Test
    @DisplayName("Should parse integer amounts as decimals")
    void shouldParseIntegerAmountsAsDecimals() {
        BigDecimal result = commandParser.parseAmount(AMOUNT_100);
        assertEquals(new BigDecimal(AMOUNT_100_00), result);
        assertEquals(2, result.scale());
    }

    @Test
    @DisplayName("Should return null for invalid amounts")
    void shouldReturnNullForInvalidAmounts() {
        assertNull(commandParser.parseAmount(INVALID_AMOUNT));
        assertNull(commandParser.parseAmount(""));
        assertNull(commandParser.parseAmount(null));
        assertNull(commandParser.parseAmount(AMOUNT_NEGATIVE_100_00));
        assertNull(commandParser.parseAmount(AMOUNT_0_00));
        assertNull(commandParser.parseAmount(AMOUNT_ZERO));
    }
}