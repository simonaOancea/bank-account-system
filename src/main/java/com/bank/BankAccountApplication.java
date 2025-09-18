package com.bank;

import com.bank.repository.inmemory.InMemoryAccountRepository;
import com.bank.cli.BankCLI;
import com.bank.repository.AccountRepository;
import com.bank.service.BankAccountService;
import com.bank.service.SimpleAccountNumberGenerator;

public class BankAccountApplication {
    
    public static void main(String[] args) {
        AccountRepository repository = new InMemoryAccountRepository();
        SimpleAccountNumberGenerator accountNumberGenerator = new SimpleAccountNumberGenerator();
        BankAccountService bankService = new BankAccountService(repository, accountNumberGenerator);
        BankCLI cli = new BankCLI(bankService);
        
        cli.start();
    }
}