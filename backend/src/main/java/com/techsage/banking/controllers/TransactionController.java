package com.techsage.banking.controllers;


import com.techsage.banking.models.Transaction;
import com.techsage.banking.services.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.techsage.banking.models.dto.TransactionDto;
import com.techsage.banking.services.interfaces.*;

import java.util.List;

@RestController
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {this.transactionService = transactionService;}

    @GetMapping("/transaction/{id}")
    public Transaction getTransaction(long id) {
        return transactionService.getById(id);
    }

    @GetMapping("/transactions")
    public List<TransactionDto> getTransactions() {
        return transactionService.getAll();
    }

//    @GetMapping("/transactions/{accountId}")
//    public List<Transaction> getTransactionsByAccountId(@PathVariable long accountId) {
//        return transactionService.getByAccountId(accountId);
//    }

    @PostMapping("/transaction/create")
    public void CreateTransaction(Transaction transaction) {
        transactionService.create(transaction);
    }

    @PutMapping("/transaction/update")
    public void UpdateTransaction(Transaction transaction) {
        transactionService.update(transaction);
    }



}
