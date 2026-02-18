package com.bank.cli;

import java.util.Objects;

public enum Command {
    NEW_ACCOUNT("NewAccount"),
    DEPOSIT("Deposit"),
    WITHDRAW("Withdraw"),
    BALANCE("Balance"),
    HISTORY("History"),
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