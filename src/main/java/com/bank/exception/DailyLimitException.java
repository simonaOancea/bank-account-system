package com.bank.exception;

public class DailyLimitException extends RuntimeException {
    
    public DailyLimitException(String message) {
        super(message);
    }
    
    public DailyLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}