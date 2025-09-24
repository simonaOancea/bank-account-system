package com.bank.cli;

import java.util.Objects;

public enum Command {
    NEW_ACCOUNT("NewAccount"),
    NEW_SAVINGS_ACCOUNT("NewSavingsAccount"),
    DEPOSIT("Deposit"),
    WITHDRAW("Withdraw"),
    BALANCE("Balance"),
    TRANSFER("Transfer"),
    QUIT("Quit");

    private final String commandName;

    Command(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public static Command fromString(String commandStr) {
        if (Objects.isNull(commandStr) || commandStr.trim().isEmpty()) {
            return null;
        }
        
        String normalizedCommand = commandStr.trim();
        for (Command command : Command.values()) {
            if (command.commandName.equalsIgnoreCase(normalizedCommand)) {
                return command;
            }
        }
        return null;
    }
}