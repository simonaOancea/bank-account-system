package com.bank.cli;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CommandParser {

	private static final int REQUIRED_ARGS_TWO = 2;
	private static final int REQUIRED_ARGS_ONE = 1;
	private static final int FIRST_ARGUMENT_INDEX = 0;
	private static final int SECOND_ARGUMENT_INDEX = 1;

	public record ParsedCommand(Command command, List<String> arguments) {
	}

	public ParsedCommand parseCommand(String input) {
		if (Objects.isNull(input) || input.trim().isEmpty()) {
			return null;
		}

		String[] parts = input.trim().split("\\s+");
		if (parts.length == 0) {
			return null;
		}

		Command command = Command.fromString(parts[0]);
		if (Objects.isNull(command)) {
			return null;
		}

		List<String> arguments = parts.length > 1 ?
				Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length)) :
				List.of();

		return new ParsedCommand(command, arguments);
	}

	public BigDecimal parseAmount(String amountStr) {
		if (Objects.isNull(amountStr) || amountStr.trim().isEmpty()) {
			return null;
		}
		try {
			BigDecimal amount = new BigDecimal(amountStr.trim()).setScale(2, RoundingMode.HALF_UP);
			return amount.compareTo(BigDecimal.ZERO) > 0 ? amount : null;
		} catch (NumberFormatException e) {
			return null;
		}
	}

	boolean isValidNewAccountCommand(List<String> arguments) {
		return arguments.size() == REQUIRED_ARGS_TWO &&
				!arguments.get(FIRST_ARGUMENT_INDEX).trim().isEmpty() &&
				!arguments.get(SECOND_ARGUMENT_INDEX).trim().isEmpty();
	}

	boolean isValidDepositCommand(List<String> arguments) {
		return arguments.size() == REQUIRED_ARGS_TWO &&
				isValidAmount(arguments.get(FIRST_ARGUMENT_INDEX)) &&
				!arguments.get(SECOND_ARGUMENT_INDEX).trim().isEmpty();
	}

	boolean isValidWithdrawCommand(List<String> arguments) {
		return arguments.size() == REQUIRED_ARGS_TWO &&
				isValidAmount(arguments.get(FIRST_ARGUMENT_INDEX)) &&
				!arguments.get(SECOND_ARGUMENT_INDEX).trim().isEmpty();
	}

	boolean isValidBalanceCommand(List<String> arguments) {
		return arguments.size() == REQUIRED_ARGS_ONE &&
				!arguments.get(FIRST_ARGUMENT_INDEX).trim().isEmpty();
	}

	private boolean isValidAmount(String amountStr) {
		return Objects.nonNull(parseAmount(amountStr));
	}
}