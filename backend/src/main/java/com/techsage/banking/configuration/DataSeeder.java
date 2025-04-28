package com.techsage.banking.configuration;

import com.techsage.banking.helpers.*;
import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.Transaction;
import com.techsage.banking.models.User;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.services.interfaces.BankAccountService;
import com.techsage.banking.services.interfaces.TransactionService;
import com.techsage.banking.services.interfaces.UserService;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

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
        User adminUser = new User(null,"John","Admin", "johnadmin@example.com","+31600000000","429731681","password123", Arrays.asList(UserRole.EMPLOYEE, UserRole.ADMIN), 100.0,100.0, LocalDateTime.now(),null, LocalDateTime.now(), UserStatus.ACTIVE, new ArrayList<>());
        userService.create(adminUser);
        User employeeUser = new User(null,"John","Employee", "johnemployee@example.com","+31600000000","297552028","password123", List.of(UserRole.EMPLOYEE), 100.0,100.0, LocalDateTime.now(),null, LocalDateTime.now(), UserStatus.ACTIVE, new ArrayList<>());
        userService.create(employeeUser);
        User customer1User = new User(null,"John","Customer", "johncustomer@example.com","+31600000000","313278994","password123", List.of(UserRole.CUSTOMER), 100.0,100.0, LocalDateTime.now(),null, LocalDateTime.now(), UserStatus.ACTIVE, new ArrayList<>());
        userService.create(customer1User);
        User customer2User = new User(null,"Emma","Customer", "emmacustomer@example.com","+31600000000","092736233","password123", List.of(UserRole.CUSTOMER), 100.0,100.0, LocalDateTime.now(),null, LocalDateTime.now(), UserStatus.ACTIVE, new ArrayList<>());
        userService.create(customer2User);

        Iban customer1CheckingIban = IbanHelper.generateIban();
        Iban customer1SavingsIban = IbanHelper.generateIban();
        Iban customer2CheckingIban = IbanHelper.generateIban();
        Iban customer2SavingsIban = IbanHelper.generateIban();

        BankAccount customer1BankAccount = new BankAccount(null, customer1User, customer1CheckingIban, 100.0, 100, BankAccountType.CHECKING, new ArrayList<>(), new ArrayList<>());
        bankAccountService.create(customer1BankAccount);
        BankAccount customer1SavingsAccount = new BankAccount(null, customer1User, customer1SavingsIban, 3500.0, 100, BankAccountType.SAVINGS, new ArrayList<>(), new ArrayList<>());
        bankAccountService.create(customer1SavingsAccount);

        BankAccount customer2BankAccount = new BankAccount(null, customer2User, customer2CheckingIban, 100.0, 100, BankAccountType.CHECKING, new ArrayList<>(), new ArrayList<>());
        bankAccountService.create(customer2BankAccount);
        BankAccount customer2SavingsAccount = new BankAccount(null, customer2User, customer2SavingsIban, 15000.0, 100, BankAccountType.SAVINGS, new ArrayList<>(), new ArrayList<>());
        bankAccountService.create(customer2SavingsAccount);

        createTestTransactions(customer1User, customer1BankAccount, customer2BankAccount, 50.0);
        createTestTransactions(customer1User, customer1SavingsAccount, customer1BankAccount, 500.0);
        createTestTransactions(customer2User, customer2BankAccount, customer2SavingsAccount, 50.0);
    }

    private void createTestTransactions(User initiator, BankAccount fromAccount, BankAccount toAccount, double amount) {
        LocalDateTime now = LocalDateTime.now();

        // Sender transaction - withdrawal
        Transaction senderTransaction = new Transaction(
                null,
                fromAccount,
                toAccount,
                initiator,
                amount,
                now,
                TransactionType.WITHDRAWAL,
                "Transfer to " + toAccount.getIban()
        );
        transactionService.create(senderTransaction);

        // Receiver transaction - deposit
        Transaction receiverTransaction = new Transaction(
                null,
                fromAccount,
                toAccount,
                initiator,
                amount,
                now,
                TransactionType.DEPOSIT,
                "Transfer from " + fromAccount.getIban()
        );
        transactionService.create(receiverTransaction);
    }
}
