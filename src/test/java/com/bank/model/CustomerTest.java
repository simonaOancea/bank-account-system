package com.bank.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String ALTERNATIVE_FIRST_NAME = "Jane";
    private static final String TEST_FULL_NAME = "John Doe";
    private static final String WHITESPACE_ONLY = "   ";
    
    private static final String FIRST_NAME_NULL_OR_EMPTY_ERROR = "First name cannot be null or empty";
    private static final String LAST_NAME_NULL_OR_EMPTY_ERROR = "Last name cannot be null or empty";

    @Test
    @DisplayName("Should create customer with valid names")
    void shouldCreateCustomerWithValidNames() {
        Customer customer = new Customer(TEST_FIRST_NAME, TEST_LAST_NAME);
        
        assertEquals(TEST_FIRST_NAME, customer.firstName());
        assertEquals(TEST_LAST_NAME, customer.lastName());
        assertEquals(TEST_FULL_NAME, customer.getFullName());
    }

    @Test
    @DisplayName("Should trim whitespace from names")
    void shouldTrimWhitespaceFromNames() {
        Customer customer = new Customer("  " + TEST_FIRST_NAME + "  ", "  " + TEST_LAST_NAME + "  ");
        
        assertEquals(TEST_FIRST_NAME, customer.firstName());
        assertEquals(TEST_LAST_NAME, customer.lastName());
    }

    @Test
    @DisplayName("Should throw exception for null first name")
    void shouldThrowExceptionForNullFirstName() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Customer(null, TEST_LAST_NAME)
        );
        assertEquals(FIRST_NAME_NULL_OR_EMPTY_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for empty first name")
    void shouldThrowExceptionForEmptyFirstName() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Customer("", TEST_LAST_NAME)
        );
        assertEquals(FIRST_NAME_NULL_OR_EMPTY_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for whitespace-only first name")
    void shouldThrowExceptionForWhitespaceOnlyFirstName() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Customer(WHITESPACE_ONLY, TEST_LAST_NAME)
        );
        assertEquals(FIRST_NAME_NULL_OR_EMPTY_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for null last name")
    void shouldThrowExceptionForNullLastName() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Customer(TEST_FIRST_NAME, null)
        );
        assertEquals(LAST_NAME_NULL_OR_EMPTY_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for empty last name")
    void shouldThrowExceptionForEmptyLastName() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Customer(TEST_FIRST_NAME, "")
        );
        assertEquals(LAST_NAME_NULL_OR_EMPTY_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void shouldImplementEqualsCorrectly() {
        Customer customer1 = new Customer(TEST_FIRST_NAME, TEST_LAST_NAME);
        Customer customer2 = new Customer(TEST_FIRST_NAME, TEST_LAST_NAME);
        Customer customer3 = new Customer(ALTERNATIVE_FIRST_NAME, TEST_LAST_NAME);
        
        assertEquals(customer1, customer2);
        assertNotEquals(customer1, customer3);
        assertNotEquals(null, customer1);
        assertNotEquals("not a customer", customer1);
    }

    @Test
    @DisplayName("Should implement hashCode correctly")
    void shouldImplementHashCodeCorrectly() {
        Customer customer1 = new Customer(TEST_FIRST_NAME, TEST_LAST_NAME);
        Customer customer2 = new Customer(TEST_FIRST_NAME, TEST_LAST_NAME);
        
        assertEquals(customer1.hashCode(), customer2.hashCode());
    }

    @Test
    @DisplayName("Should implement toString correctly")
    void shouldImplementToStringCorrectly() {
        Customer customer = new Customer(TEST_FIRST_NAME, TEST_LAST_NAME);
        String expectedString = "Customer[firstName=" + TEST_FIRST_NAME + ", lastName=" + TEST_LAST_NAME + "]";
        
        assertEquals(expectedString, customer.toString());
    }
}