package com.bank;

import com.bank.repository.TransactionRepository;
import com.bank.repository.inmemory.InMemoryAccountRepository;
import com.bank.cli.BankCLI;
import com.bank.repository.AccountRepository;
import com.bank.repository.inmemory.InMemoryTransactionRepository;
import com.bank.service.BankAccountService;
import com.bank.service.SimpleAccountNumberGenerator;

public class BankAccountApplication {
    
    public static void main(String[] args) {
        AccountRepository repository = new InMemoryAccountRepository();
        TransactionRepository transactionRepository = new InMemoryTransactionRepository();
        SimpleAccountNumberGenerator accountNumberGenerator = new SimpleAccountNumberGenerator();
        BankAccountService bankService = new BankAccountService(repository, transactionRepository, accountNumberGenerator);
        BankCLI cli = new BankCLI(bankService);
        
        cli.start();
    }
}