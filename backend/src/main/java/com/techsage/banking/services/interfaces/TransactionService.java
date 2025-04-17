package com.techsage.banking.services.interfaces;

import com.techsage.banking.models.Transaction;

import java.util.List;

public interface TransactionService {
    List<Transaction> getAll();
    Transaction getById(int id);
    Transaction create(Transaction transaction);
    Transaction update(Transaction transaction);
    void delete(int id);
}
