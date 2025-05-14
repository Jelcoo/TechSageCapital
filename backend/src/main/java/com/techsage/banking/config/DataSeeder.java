package com.techsage.banking.config;

import com.techsage.banking.exceptions.TransactionException;
import com.techsage.banking.helpers.*;
import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.Transaction;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.TransactionDto;
import com.techsage.banking.models.dto.requests.TransactionRequestDto;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.services.interfaces.BankAccountService;
import com.techsage.banking.services.interfaces.TransactionService;
import com.techsage.banking.services.interfaces.UserService;
import org.iban4j.Iban;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.*;
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
        User adminUser = new User(null,"John","Admin", "johnadmin@example.com","+31600000000","429731681","password123", Arrays.asList(UserRole.ROLE_USER, UserRole.ROLE_EMPLOYEE, UserRole.ROLE_ADMIN), BigDecimal.valueOf(100.0), BigDecimal.valueOf(100.0), LocalDateTime.now(),null, LocalDateTime.now(), UserStatus.ACTIVE, new ArrayList<>());
        userService.create(adminUser);
        User employeeUser = new User(null,"John","Employee", "johnemployee@example.com","+31600000000","297552028","password123", List.of(UserRole.ROLE_USER, UserRole.ROLE_EMPLOYEE), BigDecimal.valueOf(100.0),BigDecimal.valueOf(100.0), LocalDateTime.now(),null, LocalDateTime.now(), UserStatus.ACTIVE, new ArrayList<>());
        userService.create(employeeUser);
        User customer1User = new User(null,"John","Customer", "johncustomer@example.com","+31600000000","313278994","password123", List.of(UserRole.ROLE_USER, UserRole.ROLE_CUSTOMER), BigDecimal.valueOf(100.0),BigDecimal.valueOf(100.0), LocalDateTime.now(),null, LocalDateTime.now(), UserStatus.ACTIVE, new ArrayList<>());
        userService.create(customer1User);
        User customer2User = new User(null,"Emma","Customer", "emmacustomer@example.com","+31600000000","092736233","password123", List.of(UserRole.ROLE_USER, UserRole.ROLE_CUSTOMER), BigDecimal.valueOf(100.0),BigDecimal.valueOf(100.0), LocalDateTime.now(),null, LocalDateTime.now(), UserStatus.ACTIVE, new ArrayList<>());
        userService.create(customer2User);
        User customer3User = new User(null,"Henk","Customer", "henkcustomer@example.com","+31600000000","642590473","password123", List.of(UserRole.ROLE_USER, UserRole.ROLE_CUSTOMER), BigDecimal.valueOf(0.0),BigDecimal.valueOf(0.0), LocalDateTime.now(),null, LocalDateTime.now(), UserStatus.PENDING, new ArrayList<>());
        userService.create(customer3User);

        BankAccount customer1BankAccount = bankAccountService.create(customer1User, BankAccountType.CHECKING, BigDecimal.valueOf(100), BigDecimal.valueOf(1000.0));
        BankAccount customer1SavingsAccount = bankAccountService.create(customer1User, BankAccountType.SAVINGS, BigDecimal.valueOf(0), BigDecimal.valueOf(3500.0));
        BankAccount customer2BankAccount = bankAccountService.create(customer2User, BankAccountType.CHECKING, BigDecimal.valueOf(100), BigDecimal.valueOf(1000.0));
        BankAccount customer2SavingsAccount = bankAccountService.create(customer2User, BankAccountType.SAVINGS, BigDecimal.valueOf(0), BigDecimal.valueOf(15000.0));

        createTestTransactions(customer1User, customer1BankAccount, customer2BankAccount, BigDecimal.valueOf(50.0));
        createTestTransactions(customer1User, customer1SavingsAccount, customer1BankAccount, BigDecimal.valueOf(500.0));
        createTestTransactions(customer2User, customer2BankAccount, customer2SavingsAccount, BigDecimal.valueOf(50.0));
    }

    private void createTestTransactions(User initiator, BankAccount fromAccount, BankAccount toAccount, BigDecimal amount) {
        TransactionRequestDto transaction = new TransactionRequestDto(
                fromAccount.getIban().toString(),
                toAccount.getIban().toString(),
                amount,
                "Test transaction"
        );
        try {
            transactionService.create(transaction, initiator);
        } catch (TransactionException e) {
            System.out.println(e.getMessage());
        }
    }
}
