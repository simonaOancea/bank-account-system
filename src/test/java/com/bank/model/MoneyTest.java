package com.bank.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    private static final String TEST_AMOUNT_123_45 = "123.45";
    private static final String TEST_AMOUNT_123_456 = "123.456";
    private static final String TEST_AMOUNT_123_46 = "123.46";
    private static final String TEST_AMOUNT_100_00 = "100.00";
    private static final String TEST_AMOUNT_100_25 = "100.25";
    private static final String TEST_AMOUNT_100_75 = "100.75";
    private static final String TEST_AMOUNT_50_00 = "50.00";
    private static final String TEST_AMOUNT_50_75 = "50.75";
    private static final String TEST_AMOUNT_25_25 = "25.25";
    private static final String TEST_AMOUNT_75_50 = "75.50";
    private static final String TEST_AMOUNT_151_00 = "151.00";
    private static final String TEST_AMOUNT_150_00 = "150.00";
    private static final String TEST_AMOUNT_200_00 = "200.00";
    private static final String TEST_AMOUNT_0_00 = "0.00";
    private static final String TEST_AMOUNT_0_01 = "0.01";
    private static final String NEGATIVE_AMOUNT_100_00 = "-100.00";
    private static final String FORMATTED_123_45 = "$123.45";
    private static final String INVALID_FORMAT = "invalid";
    
    private static final String AMOUNT_NULL_ERROR = "Amount cannot be null";
    private static final String AMOUNT_NULL_OR_EMPTY_ERROR = "Amount cannot be null or empty";
    private static final String INVALID_AMOUNT_FORMAT_ERROR = "Invalid amount format";

    @Test
    @DisplayName("Should create money from BigDecimal")
    void shouldCreateMoneyFromBigDecimal() {
        Money money = Money.of(new BigDecimal(TEST_AMOUNT_123_45));
        
        assertEquals(new BigDecimal(TEST_AMOUNT_123_45), money.getAmount());
        assertEquals(TEST_AMOUNT_123_45, money.toString());
    }

    @Test
    @DisplayName("Should create money from string")
    void shouldCreateMoneyFromString() {
        Money money = Money.of(TEST_AMOUNT_123_45);
        
        assertEquals(new BigDecimal(TEST_AMOUNT_123_45), money.getAmount());
    }

    @Test
    @DisplayName("Should create money from double")
    void shouldCreateMoneyFromDouble() {
        Money money = Money.of(Double.parseDouble(TEST_AMOUNT_123_45));
        
        assertEquals(new BigDecimal(TEST_AMOUNT_123_45), money.getAmount());
    }

    @Test
    @DisplayName("Should handle proper decimal scaling")
    void shouldHandleProperDecimalScaling() {
        Money money = Money.of(TEST_AMOUNT_123_456);
        
        assertEquals(new BigDecimal(TEST_AMOUNT_123_46), money.getAmount());
        assertEquals(2, money.getAmount().scale());
    }

    @Test
    @DisplayName("Should add money correctly")
    void shouldAddMoneyCorrectly() {
        Money money1 = Money.of(TEST_AMOUNT_100_25);
        Money money2 = Money.of(TEST_AMOUNT_50_75);
        
        Money result = money1.add(money2);
        
        assertEquals(Money.of(TEST_AMOUNT_151_00), result);
    }

    @Test
    @DisplayName("Should subtract money correctly")
    void shouldSubtractMoneyCorrectly() {
        Money money1 = Money.of(TEST_AMOUNT_100_75);
        Money money2 = Money.of(TEST_AMOUNT_25_25);
        
        Money result = money1.subtract(money2);
        
        assertEquals(Money.of(TEST_AMOUNT_75_50), result);
    }

    @Test
    @DisplayName("Should multiply by double")
    void shouldMultiplyByDouble() {
        Money money = Money.of(TEST_AMOUNT_100_00);
        
        Money result = money.multiply(1.5);
        
        assertEquals(Money.of(TEST_AMOUNT_150_00), result);
    }

    @Test
    @DisplayName("Should check if positive")
    void shouldCheckIfPositive() {
        assertTrue(Money.of(TEST_AMOUNT_100_00).isPositive());
        assertFalse(Money.of(NEGATIVE_AMOUNT_100_00).isPositive());
        assertFalse(Money.ZERO.isPositive());
    }

    @Test
    @DisplayName("Should check if negative")
    void shouldCheckIfNegative() {
        assertTrue(Money.of(NEGATIVE_AMOUNT_100_00).isNegative());
        assertFalse(Money.of(TEST_AMOUNT_100_00).isNegative());
        assertFalse(Money.ZERO.isNegative());
    }

    @Test
    @DisplayName("Should check if zero")
    void shouldCheckIfZero() {
        assertTrue(Money.ZERO.isZero());
        assertTrue(Money.of(TEST_AMOUNT_0_00).isZero());
        assertFalse(Money.of(TEST_AMOUNT_0_01).isZero());
    }

    @Test
    @DisplayName("Should compare money amounts")
    void shouldCompareMoneyAmounts() {
        Money money1 = Money.of(TEST_AMOUNT_100_00);
        Money money2 = Money.of(TEST_AMOUNT_50_00);
        Money money3 = Money.of(TEST_AMOUNT_100_00);
        
        assertTrue(money1.isGreaterThan(money2));
        assertFalse(money2.isGreaterThan(money1));
        assertTrue(money2.isLessThan(money1));
        assertTrue(money1.isGreaterThanOrEqualTo(money3));
        assertTrue(money2.isLessThanOrEqualTo(money1));
        assertTrue(money1.isLessThanOrEqualTo(money3));
        assertEquals(0, money1.compareTo(money3));
    }

    @Test
    @DisplayName("Should throw exception for null BigDecimal")
    void shouldThrowExceptionForNullBigDecimal() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Money.of((BigDecimal) null)
        );
        assertEquals(AMOUNT_NULL_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for null string")
    void shouldThrowExceptionForNullString() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Money.of((String) null)
        );
        assertEquals(AMOUNT_NULL_OR_EMPTY_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for invalid string format")
    void shouldThrowExceptionForInvalidStringFormat() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Money.of(INVALID_FORMAT)
        );
        assertTrue(exception.getMessage().contains(INVALID_AMOUNT_FORMAT_ERROR));
    }

    @Test
    @DisplayName("Should format string representation")
    void shouldFormatStringRepresentation() {
        Money money = Money.of(TEST_AMOUNT_123_45);
        
        assertEquals(TEST_AMOUNT_123_45, money.toString());
        assertEquals(FORMATTED_123_45, money.toFormattedString());
    }

    @Test
    @DisplayName("Should implement equals and hashCode correctly")
    void shouldImplementEqualsAndHashCodeCorrectly() {
        Money money1 = Money.of(TEST_AMOUNT_100_00);
        Money money2 = Money.of(TEST_AMOUNT_100_00);
        Money money3 = Money.of(TEST_AMOUNT_200_00);
        
        assertEquals(money1, money2);
        assertNotEquals(money1, money3);
        assertEquals(money1.hashCode(), money2.hashCode());
    }

    @Test
    @DisplayName("Should be immutable")
    void shouldBeImmutable() {
        Money money1 = Money.of(TEST_AMOUNT_100_00);
        Money money2 = money1.add(Money.of(TEST_AMOUNT_50_00));
        
        assertEquals(Money.of(TEST_AMOUNT_100_00), money1);
        assertEquals(Money.of(TEST_AMOUNT_150_00), money2);
        assertNotSame(money1, money2);
    }
}