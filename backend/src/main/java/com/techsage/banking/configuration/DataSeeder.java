package com.techsage.banking.configuration;

import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.Transaction;
import com.techsage.banking.models.User;
import com.techsage.banking.services.interfaces.BankAccountService;
import com.techsage.banking.services.interfaces.TransactionService;
import com.techsage.banking.services.interfaces.UserService;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class DataSeeder implements ApplicationRunner {
    private final UserService userService;
    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;

    public DataSeeder(UserService userService, BankAccountService bankAccountService, TransactionService transactionService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
        this.transactionService = transactionService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User user = new User(null,"s","s", "s","s",1,"s","s", User.Role.ADMIN, 100.0,100.0, LocalDateTime.now(),"s", LocalDateTime.now(), User.Status.ACTIVE, new ArrayList<>());
        userService.create(user);
        User user2 = new User(null,"s","s", "s","s",1,"s","s", User.Role.EMPLOYEE, 100.0,100.0, LocalDateTime.now(),"s", LocalDateTime.now(), User.Status.ACTIVE, new ArrayList<>());
        userService.create(user2);
        User user3 = new User(null,"s","s", "s","s",1,"s","s", User.Role.CUSTOMER, 100.0,100.0, LocalDateTime.now(),"s", LocalDateTime.now(), User.Status.ACTIVE, new ArrayList<>());
        userService.create(user3);

        Iban iban1 = new Iban.Builder().countryCode(CountryCode.NL).bankCode("INHO").buildRandom();
        Iban iban2 = new Iban.Builder().countryCode(CountryCode.NL).bankCode("INHO").buildRandom();
        Iban iban3 = new Iban.Builder().countryCode(CountryCode.NL).bankCode("INHO").buildRandom();

        BankAccount bankAccount = new BankAccount(null, user, iban1, 100.0, 100, BankAccount.Type.SAVINGS, new ArrayList<>(), new ArrayList<>());
        bankAccountService.create(bankAccount);
        BankAccount bankAccount2 = new BankAccount(null, user2, iban2, 100.0, 100, BankAccount.Type.SAVINGS, new ArrayList<>(), new ArrayList<>());
        bankAccountService.create(bankAccount2);
        BankAccount bankAccount3 = new BankAccount(null, user3, iban3, 100.0, 100, BankAccount.Type.SAVINGS, new ArrayList<>(), new ArrayList<>());
        bankAccountService.create(bankAccount3);

        Transaction transaction1 = new Transaction(null, bankAccount, bankAccount2, user, 100.0, LocalDateTime.now(), Transaction.Type.DEPOSIT, "Deposit");
        transactionService.create(transaction1);
        Transaction transaction2 = new Transaction(null, bankAccount2, bankAccount, user2, 100.0, LocalDateTime.now(), Transaction.Type.WITHDRAWAL, "Withdrawal");
        transactionService.create(transaction2);
        Transaction transaction3 = new Transaction(null, bankAccount3, bankAccount, user3, 100.0, LocalDateTime.now(), Transaction.Type.WITHDRAWAL, "Withdrawal");
        transactionService.create(transaction3);
    }
}
