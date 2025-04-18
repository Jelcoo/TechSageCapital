package com.techsage.banking.controllers;

import com.techsage.banking.models.dto.BankAccountDto;
import com.techsage.banking.models.dto.TransactionDto;
import com.techsage.banking.models.dto.UserDto;
import com.techsage.banking.services.interfaces.BankAccountService;
import com.techsage.banking.services.interfaces.TransactionService;
import com.techsage.banking.services.interfaces.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DemoController {
    private final UserService userService;
    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;

    public DemoController(UserService userService, BankAccountService bankAccountService, TransactionService transactionService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
        this.transactionService = transactionService;
    }

    @GetMapping("/users")
    public List<UserDto> users() {
        return userService.getAll();
    }

    @GetMapping("/bankaccounts")
    public List<BankAccountDto> bankAccounts() {
        return bankAccountService.getAll();
    }

    @GetMapping("/transactions")
    public List<TransactionDto> transaction() {
        return transactionService.getAll();
    }
}
