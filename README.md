# Bank Account System

A simple text-based command line bank account program that allows a bank teller to:
1. Create a new bank account for a customer
2. Deposit and withdraw cash to/from the account for a customer  
3. Display the balance of the account
4. Quit the program

**Note**: This program does not persist any data; data is lost when the program exits.

## Features

- Create new bank accounts for customers
- Deposit and withdraw cash to/from accounts
- Display account balances
- Input validation and error handling
- Thread-safe operations
- Comprehensive test coverage

## Requirements

- Java 17 or higher
- Maven 3.6 or higher

## Getting Started

### Building the Project

```bash
cd bank-account-system
mvn clean compile
```

### Running Tests

Run all tests with coverage report:

```bash
mvn clean test
```

View test coverage report:

```bash
mvn jacoco:report
open target/site/jacoco/index.html
```

### Running the Application

```bash
mvn exec:java -Dexec.mainClass="com.bank.BankAccountApplication"
```

Or compile and run with Java directly:

```bash
mvn clean package
java -cp target/classes com.bank.BankAccountApplication
```

## Usage

Once the application starts, you'll see a welcome message with available commands. Enter commands at the `>` prompt.

### Available Commands

| Command | Format | Description |
|---------|--------|-------------|
| `NewAccount` | `NewAccount [First Name] [Last Name]` | Creates a new account using the entered first name and last name of the account holder. Prints out the account number for the newly created account. |
| `Deposit` | `Deposit [Amount] [Account Number]` | Deposits the specified amount into the provided account number. |
| `Withdraw` | `Withdraw [Amount] [Account Number]` | Withdraws the specified amount from the provided account number. |
| `Balance` | `Balance [Account Number]` | Shows current account balance |
| `Quit` | `Quit` | Quits the program |

### Examples

- `NewAccount John Doe` - Creates a new account for John Doe
- `Deposit 12.50 1000001` - Deposits 12.50 into account number 1000001  
- `Withdraw 5.00 1000001` - Withdraws 5.00 from account number 1000001
- `Balance 1000001` - Shows current balance for account 1000001
- `Quit` - Exits the program

### Example Session

```
Welcome to Bank Account System
Available commands:
  NewAccount [First Name] [Last Name] - Create a new account
  Deposit [Amount] [Account Number] - Deposit money
  Withdraw [Amount] [Account Number] - Withdraw money
  Balance [Account Number] - Check balance
  Quit - Exit the program

> NewAccount Alice Smith
Account created successfully. Account number: 1000001

> Deposit 500.00 1000001
Deposited $500.00 to account 1000001

> Balance 1000001
Account 1000001 balance: $500.00

> Withdraw 150.25 1000001
Withdrew $150.25 from account 1000001

> Balance 1000001
Account 1000001 balance: $349.75

> Quit
Thank you for using Bank Account System!
```

## Architecture

### Project Structure

```
src/
├── main/java/com/bank/
│   ├── BankAccountApplication.java           # Main application entry point
│   ├── cli/                                 # Command-line interface
│   │   ├── BankCLI.java                    # Main CLI controller
│   │   ├── Command.java                    # Command enumeration
│   │   └── CommandParser.java              # Command parsing logic
│   ├── exception/                          # Custom exceptions
│   │   └── AccountNotFoundException.java
│   ├── model/                              # Domain models
│   │   ├── Account.java                   # Account entity
│   │   ├── Customer.java                  # Customer record
│   │   └── Money.java                     # Money value object
│   ├── repository/                         # Repository interfaces
│   │   ├── AccountRepository.java         # Repository interface
│   │   └── inmemory/                      # In-memory implementations
│   │       └── InMemoryAccountRepository.java
│   └── service/                           # Business logic layer
│       ├── AccountNumberGenerator.java    # Interface for account number generation
│       ├── BankAccountService.java        # Main business service
│       └── SimpleAccountNumberGenerator.java # Simple implementation
└── test/java/com/bank/                    # Comprehensive test suite
    ├── cli/
    │   ├── BankCLITest.java
    │   └── CommandParserTest.java
    ├── model/
    │   ├── AccountTest.java
    │   ├── CustomerTest.java
    │   └── MoneyTest.java
    └── service/
        ├── BankAccountServiceTest.java
        └── SimpleAccountNumberGeneratorTest.java
```

### Design Principles

- **Hexagonal Architecture**: Repository pattern separates domain logic from data access concerns
- **Separation of Concerns**: Clear separation between CLI, business logic, domain models, and data access
- **Dependency Injection**: Services are injected rather than created directly
- **Immutable Value Objects**: Money value object and Customer record are immutable
- **Thread Safety**: Repository layer uses concurrent data structures for safe multi-threaded access
- **Input Validation**: Comprehensive validation at CLI and service layers
- **Error Handling**: Proper exception handling with meaningful, user-friendly messages

### Key Design Decisions

1. **Money Value Object**: Dedicated `Money` class using `BigDecimal` to avoid floating-point precision issues
2. **Java Records**: Customer implemented as a record with compact constructor for immutability and validation
3. **Repository Pattern**: `AccountRepository` interface with `InMemoryAccountRepository` implementation for data access abstraction
4. **Account Number Generation**: Abstracted behind `AccountNumberGenerator` interface for flexibility
5. **Command Pattern**: CLI commands are parsed into structured `ParsedCommand` objects
6. **Thread-Safe Repository**: `ConcurrentHashMap` with atomic operations for safe concurrent account access
7. **Validation Architecture**: Multi-layer validation (CLI parsing, service layer, domain models)

## Testing

The project includes comprehensive test coverage:

- **Unit Tests**: Test individual components in isolation
- **Component Tests**: Test CLI workflow and command processing
- **Thread Safety Tests**: Verify concurrent access scenarios
- **Edge Case Coverage**: Negative scenarios, validation, and error conditions

### Test Categories

- `CustomerTest`: Domain model validation
- `AccountTest`: Account operations and business rules
- `BankAccountServiceTest`: Service layer business logic
- `CommandParserTest`: Command parsing and validation
- `BankCLITest`: CLI component testing and workflow validation
- `SimpleAccountNumberGeneratorTest`: Account number generation including thread safety

Run tests with:

```bash
mvn test
```

## Error Handling

The application handles various error scenarios:

- **Invalid Commands**: Unknown commands show usage help
- **Validation Errors**: Invalid amounts, empty fields, etc.
- **Business Rule Violations**: Insufficient funds, negative amounts
- **Missing Accounts**: Attempting operations on non-existent accounts
- **Input Errors**: Malformed input, unexpected characters

## Limitations

- **No Persistence**: Data is lost when the program exits (by design)
- **Single User**: No multi-user authentication or authorization
- **Simple Account Types**: Only basic savings accounts supported
- **No Transaction History**: No audit trail of operations
- **No Interest Calculation**: No interest accrual features


