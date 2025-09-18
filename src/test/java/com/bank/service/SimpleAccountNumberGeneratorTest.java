package com.bank.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class SimpleAccountNumberGeneratorTest {

    @Test
    @DisplayName("Should generate valid account numbers")
    void shouldGenerateValidAccountNumbers() {
        SimpleAccountNumberGenerator generator = new SimpleAccountNumberGenerator();
        
        String accountNumber = generator.generateAccountNumber();
        
        assertNotNull(accountNumber);
        assertFalse(accountNumber.isEmpty());
        assertTrue(accountNumber.matches("\\d+"));
    }

    @Test
    @DisplayName("Should generate unique account numbers")
    void shouldGenerateUniqueAccountNumbers() {
        SimpleAccountNumberGenerator generator = new SimpleAccountNumberGenerator();
        
        String accountNumber1 = generator.generateAccountNumber();
        String accountNumber2 = generator.generateAccountNumber();
        
        assertNotEquals(accountNumber1, accountNumber2);
    }

    @Test
    @DisplayName("Should generate sequential account numbers")
    void shouldGenerateSequentialAccountNumbers() {
        SimpleAccountNumberGenerator generator = new SimpleAccountNumberGenerator();
        
        String accountNumber1 = generator.generateAccountNumber();
        String accountNumber2 = generator.generateAccountNumber();
        
        long number1 = Long.parseLong(accountNumber1);
        long number2 = Long.parseLong(accountNumber2);
        
        assertEquals(number1 + 1, number2);
    }

    @Test
    @DisplayName("Should start with number greater than 1000000")
    void shouldStartWithNumberGreaterThan1000000() {
        SimpleAccountNumberGenerator generator = new SimpleAccountNumberGenerator();
        
        String accountNumber = generator.generateAccountNumber();
        long number = Long.parseLong(accountNumber);
        
        assertTrue(number > 1000000);
    }

    @Test
    @DisplayName("Should be thread-safe")
    void shouldBeThreadSafe() throws InterruptedException {
        SimpleAccountNumberGenerator generator = new SimpleAccountNumberGenerator();
        int threadCount = 10;
        int numbersPerThread = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Set<String> accountNumbers = ConcurrentHashMap.newKeySet();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < numbersPerThread; j++) {
                        String accountNumber = generator.generateAccountNumber();
                        accountNumbers.add(accountNumber);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executorService.shutdown();
        
        assertEquals(threadCount * numbersPerThread, accountNumbers.size());
    }
}