package com.bank.service;

import java.util.concurrent.atomic.AtomicLong;

public class SimpleAccountNumberGenerator implements AccountNumberGenerator {
    private final AtomicLong counter = new AtomicLong(1000000);

    /**
     * Generates unique account numbers starting from 1000001 using AtomicLong for thread safety.
     * Each call returns a sequential account number, safe for concurrent access.
     * 
     * @return a unique account number as a string
     */
    @Override
    public String generateAccountNumber() {
        return String.valueOf(counter.incrementAndGet());
    }
}