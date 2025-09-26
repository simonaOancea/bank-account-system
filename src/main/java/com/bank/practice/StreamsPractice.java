package com.bank.practice;

import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.Money;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * Practice class for learning lambdas and streams in banking context.
 * Run the main method and uncomment exercises one by one to practice.
 */
public class StreamsPractice {

    public static void main(String[] args) {
        StreamsPractice practice = new StreamsPractice();

        System.out.println("=== STREAMS PRACTICE ===\n");

        // Uncomment each exercise as you work through them
        practice.exercise1_BasicFiltering();
        practice.exercise2_Mapping();
        practice.exercise3_TransactionFiltering();
        // practice.exercise4_Sorting();
        // practice.exercise5_Grouping();
        // practice.exercise6_Reducing();
        // practice.exercise7_Challenges();

        System.out.println("\n=== PRACTICE COMPLETE ===");
    }

    // Sample data setup
    private List<Account> createSampleAccounts() {
        Customer john = new Customer("John", "Doe");
        Customer jane = new Customer("Jane", "Smith");
        Customer bob = new Customer("Bob", "Johnson");
        Customer alice = new Customer("Alice", "Smith");

        Account account1 = new Account("ACC001", john);
        account1.deposit(Money.of("1500.00"));

        Account account2 = new Account("ACC002", jane);
        account2.deposit(Money.of("750.50"));

        Account account3 = new Account("ACC003", bob);
        account3.deposit(Money.of("2200.75"));

        Account account4 = new Account("ACC004", alice);
        account4.deposit(Money.of("450.00"));

        return Arrays.asList(account1, account2, account3, account4);
    }

    private List<Transaction> createSampleTransactions() {
        return Arrays.asList(
            new Transaction("TXN001", "ACC001", TransactionType.DEPOSIT, Money.of("500.00"), Money.of("1500.00"), LocalDateTime.now().minusDays(5)),
            new Transaction("TXN002", "ACC001", TransactionType.WITHDRAW, Money.of("200.00"), Money.of("1300.00"), LocalDateTime.now().minusDays(3)),
            new Transaction("TXN003", "ACC002", TransactionType.DEPOSIT, Money.of("750.50"), Money.of("750.50"), LocalDateTime.now().minusDays(7)),
            new Transaction("TXN004", "ACC003", TransactionType.DEPOSIT, Money.of("1000.00"), Money.of("2000.00"), LocalDateTime.now().minusDays(2)),
            new Transaction("TXN005", "ACC003", TransactionType.WITHDRAW, Money.of("300.00"), Money.of("1700.00"), LocalDateTime.now().minusDays(1)),
            new Transaction("TXN006", "ACC001", TransactionType.DEPOSIT, Money.of("100.00"), Money.of("1400.00"), LocalDateTime.now().minusHours(5)),
            new Transaction("TXN007", "ACC002", TransactionType.WITHDRAW, Money.of("50.00"), Money.of("700.50"), LocalDateTime.now().minusHours(2))
        );
    }

    // =================== EXERCISE 1: BASIC FILTERING ===================
    private void exercise1_BasicFiltering() {
        System.out.println("--- Exercise 1: Basic Filtering ---");
        List<Account> accounts = createSampleAccounts();

        System.out.println("All accounts:");
        accounts.forEach(acc -> System.out.println("  " + acc.getAccountNumber() + " - " +
            acc.getCustomer().firstName() + " " + acc.getCustomer().lastName() +
            " - " + acc.getFormattedBalance()));

        // TODO: Uncomment and complete these exercises

        // 1.1 Find accounts with balance > $1000
        System.out.println("\nAccounts with balance > $1000:");
        List<Account> highBalanceAccounts = accounts.stream()
                .filter(account -> account.getBalance().isGreaterThan(Money.of("1000")))
                .toList();

        highBalanceAccounts.forEach(acc -> System.out.println("   " + acc.getAccountNumber()));

        // 1.2 Find accounts belonging to customers with last name "Smith"
        System.out.println("\nSmith family accounts:");
        List<Account> smithAccounts = accounts.stream()
                .filter(acc -> acc.getCustomer().lastName().equals("Smith"))
                .toList();

        smithAccounts.forEach(acc -> System.out.println("  " + acc.getCustomer().firstName() + " Smith"));

        // 1.3 Check if any account has balance > $2000
        System.out.println("\nAny account with balance > $2000?");

        boolean hasHighBalance = accounts.stream()
                .anyMatch(acc -> acc.getBalance().isGreaterThan(Money.of("2000")));
        System.out.println("  " + hasHighBalance);

        System.out.println();
    }

    // =================== EXERCISE 2: MAPPING ===================
    private void exercise2_Mapping() {
        System.out.println("--- Exercise 2: Mapping ---");
        List<Account> accounts = createSampleAccounts();

        // 2.1 Get list of all account numbers
        System.out.println("All account numbers:");
        List<String> accountNumbers = accounts.stream()
                .map(Account::getAccountNumber)
                .toList();
        accountNumbers.forEach(num -> System.out.println("  " + num));

        // 2.2 Get list of customer full names
        System.out.println("\nCustomer full names:");
        List<String> customerNames = accounts.stream()
                .map(acc -> acc.getCustomer().getFullName())
                .toList();
        customerNames.forEach(name -> System.out.println("  " + name));

        // 2.3 Get list of formatted balances
        System.out.println("\nFormatted balances:");
        List<String> balances = accounts.stream()
                .map(Account::getFormattedBalance)
                .toList();
        balances.forEach(balance -> System.out.println("  " + balance));

        System.out.println();
    }

    // =================== EXERCISE 3: TRANSACTION FILTERING ===================
    private void exercise3_TransactionFiltering() {
        System.out.println("--- Exercise 3: Transaction Filtering ---");
        List<Transaction> transactions = createSampleTransactions();

        System.out.println("All transactions:");
        transactions.forEach(tx -> System.out.println("  " + tx.getTransactionId() + " - " +
            tx.getType() + " - " + tx.getAmount().toFormattedString()));

        // 3.1 Get only deposit transactions
        System.out.println("\nDeposit transactions:");
        List<Transaction> deposits = transactions.stream()
                .filter(tx -> tx.getType().equals("DEPOSIT"))
                .toList();
        deposits.forEach(tx -> System.out.println("  " + tx.getTransactionId()));

        // 3.2 Get transactions for account "ACC001"
        System.out.println("\nTransactions for ACC001:");
        // List<Transaction> acc001Transactions = transactions.stream()
        //     .filter(/* YOUR CODE HERE */)
        //     .collect(Collectors.toList());
        // acc001Transactions.forEach(tx -> System.out.println("  " + tx.getTransactionId()));

        // 3.3 Get transactions over $200
        System.out.println("\nTransactions over $200:");
        // List<Transaction> bigTransactions = transactions.stream()
        //     .filter(/* YOUR CODE HERE */)
        //     .collect(Collectors.toList());
        // bigTransactions.forEach(tx -> System.out.println("  " + tx.getTransactionId() + " - " + tx.getAmount().toFormattedString()));

        System.out.println();
    }

    // =================== EXERCISE 4: SORTING ===================
    private void exercise4_Sorting() {
        System.out.println("--- Exercise 4: Sorting ---");
        List<Transaction> transactions = createSampleTransactions();

        // 4.1 Sort transactions by timestamp (newest first)
        System.out.println("Transactions by timestamp (newest first):");
        // List<Transaction> sortedByTime = transactions.stream()
        //     .sorted(/* YOUR CODE HERE */)
        //     .collect(Collectors.toList());
        // sortedByTime.forEach(tx -> System.out.println("  " + tx.getTransactionId() + " - " + tx.getTimestamp()));

        // 4.2 Sort transactions by amount (highest first)
        System.out.println("\nTransactions by amount (highest first):");
        // List<Transaction> sortedByAmount = transactions.stream()
        //     .sorted(/* YOUR CODE HERE */)
        //     .collect(Collectors.toList());
        // sortedByAmount.forEach(tx -> System.out.println("  " + tx.getTransactionId() + " - " + tx.getAmount().toFormattedString()));

        // 4.3 Get the 3 most recent transactions
        System.out.println("\n3 most recent transactions:");
        // List<Transaction> recentTransactions = transactions.stream()
        //     .sorted(/* YOUR CODE HERE */)
        //     .limit(/* YOUR CODE HERE */)
        //     .collect(Collectors.toList());
        // recentTransactions.forEach(tx -> System.out.println("  " + tx.getTransactionId()));

        System.out.println();
    }

    // =================== EXERCISE 5: GROUPING ===================
    private void exercise5_Grouping() {
        System.out.println("--- Exercise 5: Grouping ---");
        List<Transaction> transactions = createSampleTransactions();

        // 5.1 Group transactions by type
        System.out.println("Transactions grouped by type:");
        // Map<TransactionType, List<Transaction>> groupedByType = transactions.stream()
        //     .collect(Collectors.groupingBy(/* YOUR CODE HERE */));
        // groupedByType.forEach((type, txs) -> {
        //     System.out.println("  " + type + ": " + txs.size() + " transactions");
        // });

        // 5.2 Group transactions by account number
        System.out.println("\nTransactions grouped by account:");
        // Map<String, List<Transaction>> groupedByAccount = transactions.stream()
        //     .collect(Collectors.groupingBy(/* YOUR CODE HERE */));
        // groupedByAccount.forEach((accountNum, txs) -> {
        //     System.out.println("  " + accountNum + ": " + txs.size() + " transactions");
        // });

        System.out.println();
    }

    // =================== EXERCISE 6: REDUCING ===================
    private void exercise6_Reducing() {
        System.out.println("--- Exercise 6: Reducing ---");
        List<Transaction> transactions = createSampleTransactions();

        // 6.1 Calculate total deposit amount
        System.out.println("Total deposit amount:");
        // Money totalDeposits = transactions.stream()
        //     .filter(/* YOUR CODE HERE */)
        //     .map(/* YOUR CODE HERE */)
        //     .reduce(Money.ZERO, /* YOUR CODE HERE */);
        // System.out.println("  " + totalDeposits.toFormattedString());

        // 6.2 Count withdrawal transactions
        System.out.println("\nNumber of withdrawal transactions:");
        // long withdrawalCount = transactions.stream()
        //     .filter(/* YOUR CODE HERE */)
        //     .count();
        // System.out.println("  " + withdrawalCount);

        // 6.3 Find the largest transaction amount
        System.out.println("\nLargest transaction amount:");
        // Optional<Money> largestAmount = transactions.stream()
        //     .map(/* YOUR CODE HERE */)
        //     .max(/* YOUR CODE HERE */);
        // if (largestAmount.isPresent()) {
        //     System.out.println("  " + largestAmount.get().toFormattedString());
        // }

        System.out.println();
    }

    // =================== EXERCISE 7: CHALLENGES ===================
    private void exercise7_Challenges() {
        System.out.println("--- Exercise 7: Challenges ---");
        List<Account> accounts = createSampleAccounts();
        List<Transaction> transactions = createSampleTransactions();

        // 7.1 Get account numbers for customers with first name starting with 'J' and balance > $1000
        System.out.println("Account numbers for customers with name starting 'J' and balance > $1000:");
        // List<String> jCustomerAccountsWithHighBalance = accounts.stream()
        //     .filter(/* YOUR CODE HERE */)
        //     .filter(/* YOUR CODE HERE */)
        //     .map(/* YOUR CODE HERE */)
        //     .collect(Collectors.toList());
        // jCustomerAccountsWithHighBalance.forEach(acc -> System.out.println("  " + acc));

        // 7.2 Get unique customer first names (no duplicates)
        System.out.println("\nUnique customer first names:");
        // List<String> uniqueFirstNames = accounts.stream()
        //     .map(/* YOUR CODE HERE */)
        //     .distinct()
        //     .collect(Collectors.toList());
        // uniqueFirstNames.forEach(name -> System.out.println("  " + name));

        // 7.3 Create a summary: account number -> total transaction amount for that account
        System.out.println("\nAccount transaction summaries:");
        // Map<String, Money> accountSummaries = transactions.stream()
        //     .collect(Collectors.groupingBy(
        //         /* YOUR CODE HERE */,
        //         Collectors.mapping(
        //             /* YOUR CODE HERE */,
        //             Collectors.reducing(Money.ZERO, /* YOUR CODE HERE */)
        //         )
        //     ));
        // accountSummaries.forEach((accountNum, total) -> {
        //     System.out.println("  " + accountNum + ": " + total.toFormattedString());
        // });

        System.out.println();
    }
}