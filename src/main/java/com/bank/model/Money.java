package com.bank.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value object representing money with proper precision and operations.
 * Immutable and thread-safe.
 */
@Getter
@EqualsAndHashCode
public final class Money implements Comparable<Money> {
    
    public static final int DECIMAL_PLACES = 2;
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    public static final Money ZERO = new Money(BigDecimal.ZERO);
    
    private static final String AMOUNT_NULL_ERROR = "Amount cannot be null";
    private static final String AMOUNT_NULL_OR_EMPTY_ERROR = "Amount cannot be null or empty";
    private static final String INVALID_AMOUNT_FORMAT_ERROR = "Invalid amount format: ";
    private static final String ADD_NULL_ERROR = "Cannot add null money";
    private static final String SUBTRACT_NULL_ERROR = "Cannot subtract null money";
    private static final String COMPARE_NULL_ERROR = "Cannot compare with null money";
    
    private final BigDecimal amount;
    
    private Money(BigDecimal amount) {
        this.amount = amount.setScale(DECIMAL_PLACES, ROUNDING_MODE);
    }
    
    public static Money of(BigDecimal amount) {
        if (Objects.isNull(amount)) {
            throw new IllegalArgumentException(AMOUNT_NULL_ERROR);
        }
        return new Money(amount);
    }
    
    public static Money of(String amount) {
        if (Objects.isNull(amount) || amount.trim().isEmpty()) {
            throw new IllegalArgumentException(AMOUNT_NULL_OR_EMPTY_ERROR);
        }
        try {
            return new Money(new BigDecimal(amount.trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(INVALID_AMOUNT_FORMAT_ERROR + amount, e);
        }
    }
    
    public static Money of(double amount) {
        return new Money(BigDecimal.valueOf(amount));
    }
    
    public Money add( Money other) {
        if (Objects.isNull(other)) {
            throw new IllegalArgumentException(ADD_NULL_ERROR);
        }
        return new Money(this.amount.add(other.amount));
    }
    
    public Money subtract( Money other) {
        if (Objects.isNull(other)) {
            throw new IllegalArgumentException(SUBTRACT_NULL_ERROR);
        }
        return new Money(this.amount.subtract(other.amount));
    }
    
    public Money multiply(double multiplier) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier)));
    }
    
    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }
    
    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }
    
    public boolean isGreaterThan( Money other) {
        if (Objects.isNull(other)) {
            throw new IllegalArgumentException(COMPARE_NULL_ERROR);
        }
        return this.amount.compareTo(other.amount) > 0;
    }
    
    public boolean isLessThan( Money other) {
        if (Objects.isNull(other)) {
            throw new IllegalArgumentException(COMPARE_NULL_ERROR);
        }
        return this.amount.compareTo(other.amount) < 0;
    }
    
    public boolean isGreaterThanOrEqualTo( Money other) {
        if (Objects.isNull(other)) {
            throw new IllegalArgumentException(COMPARE_NULL_ERROR);
        }
        return this.amount.compareTo(other.amount) >= 0;
    }
    
    public boolean isLessThanOrEqualTo( Money other) {
        if (Objects.isNull(other)) {
            throw new IllegalArgumentException(COMPARE_NULL_ERROR);
        }
        return this.amount.compareTo(other.amount) <= 0;
    }

	@Override
    public int compareTo( Money other) {
        if (Objects.isNull(other)) {
            throw new IllegalArgumentException(COMPARE_NULL_ERROR);
        }
        return this.amount.compareTo(other.amount);
    }
    
    @Override
    public String toString() {
        return amount.toString();
    }
    
    /**
     * Returns formatted string representation like "$123.45"
     */
    public String toFormattedString() {
        return "$" + amount.toString();
    }
}