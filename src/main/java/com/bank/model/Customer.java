package com.bank.model;

import java.util.Objects;

/**
 * Immutable value object representing a bank customer.
 */
public record Customer(String firstName, String lastName) {
    
    private static final String FIRST_NAME_ERROR = "First name cannot be null or empty";
    private static final String LAST_NAME_ERROR = "Last name cannot be null or empty";
    
    public Customer {
        firstName = validateAndTrim(firstName, FIRST_NAME_ERROR);
        lastName = validateAndTrim(lastName, LAST_NAME_ERROR);
    }

    /**
     * Returns the customer's full name.
     * @return concatenated first and last name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    private static String validateAndTrim(String name, String errorMessage) {
        if (Objects.isNull(name) || name.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return name.trim();
    }
}