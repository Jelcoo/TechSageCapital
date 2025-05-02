package com.techsage.banking.controllers;

import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.dto.BankAccountDto;
import com.techsage.banking.services.interfaces.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/bankAccounts")
public class BankAccountController {
    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {this.bankAccountService = bankAccountService;}

    @GetMapping("/{id}")
    public BankAccount getBankAccount(@PathVariable long id) {
        return bankAccountService.getById(id);
    }

    @GetMapping("/getAll")
    public List<BankAccountDto> getBankAccounts() {
        return bankAccountService.getAll();
    }

    @PostMapping("/create")
    public void createBankAccount(@RequestBody BankAccount bankAccount) {
        bankAccountService.create(bankAccount);
    }

    @PutMapping("/update")
    public void updateBankAccount(@RequestBody BankAccount bankAccount) {
        bankAccountService.update(bankAccount);
    }
}
