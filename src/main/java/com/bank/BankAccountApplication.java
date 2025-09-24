package com.bank;

import com.bank.repository.DailyTransactionRepository;
import com.bank.repository.inmemory.InMemoryAccountRepository;
import com.bank.cli.BankCLI;
import com.bank.repository.AccountRepository;
import com.bank.repository.inmemory.InMemoryDailyTransactionTrackerRepository;
import com.bank.service.BankAccountService;
import com.bank.service.DailyLimitsService;
import com.bank.service.SimpleAccountNumberGenerator;

public class BankAccountApplication {
    
    public static void main(String[] args) {
        AccountRepository repository = new InMemoryAccountRepository();
        DailyTransactionRepository dailyTransactionRepository = new InMemoryDailyTransactionTrackerRepository();
        DailyLimitsService dailyLimitsService = new DailyLimitsService(dailyTransactionRepository);
        SimpleAccountNumberGenerator accountNumberGenerator = new SimpleAccountNumberGenerator();
        BankAccountService bankService = new BankAccountService(repository, accountNumberGenerator, dailyLimitsService);
        BankCLI cli = new BankCLI(bankService);
        
        cli.start();
    }
}