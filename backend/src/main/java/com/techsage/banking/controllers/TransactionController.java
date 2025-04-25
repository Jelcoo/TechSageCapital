package com.techsage.banking.controllers;

import com.techsage.banking.models.Transaction;
import org.springframework.web.bind.annotation.*;
import com.techsage.banking.models.dto.TransactionDto;

@RestController
public class TransactionController {

    @Autowired
    private final TransactionService transactionService;
    @GetMapping("/transaction/{id}")
    public TransactionDto getTransaction(long id) {
        return transactionService.getById(id);
    }

    @GetMapping("/transactions")
    public List<TransactionDto> getTransactions() {
        return transactionService.getAll();
    }

    @getMapping("/transactions/{accountId}")
    public List<TransactionDto> getTransactionsByAccountId(@PathVariable long accountId) {
        return transactionService.getByAccountId(accountId);
    }

    @PostMapping("/transaction/create")
    public void CreateTransaction(TransactionDto transaction) {
        transactionService.create(transaction);
    }

    @PutMapping("/transaction/update")
    public void UpdateTransaction(TransactionDto transaction) {
        transactionService.update(transaction);
    }



}
