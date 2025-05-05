package com.techsage.banking.controllers;


import com.techsage.banking.models.Transaction;
import com.techsage.banking.services.interfaces.TransactionService;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.techsage.banking.models.dto.TransactionDto;
import com.techsage.banking.services.interfaces.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transactions", description = "Endpoints for transaction management")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {this.transactionService = transactionService;}

    @GetMapping("/{id}")
    public Transaction getTransaction(long id) {
        return transactionService.getById(id);
    }

    @GetMapping
    public List<TransactionDto> getTransactions() {
        return transactionService.getAll();
    }

    @PostMapping("/create")
    public void CreateTransaction(Transaction transaction) {
        transactionService.create(transaction);
    }

    @PutMapping("/update")
    public void UpdateTransaction(Transaction transaction) {
        transactionService.update(transaction);
    }



}
