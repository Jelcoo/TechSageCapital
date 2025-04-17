package com.techsage.banking.services;

import com.techsage.banking.models.Transaction;
import com.techsage.banking.repositories.TransactionRepository;
import com.techsage.banking.services.interfaces.TransactionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceJpa implements TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionServiceJpa(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> getAll() {
        return (List<Transaction>) transactionRepository.findAll();
    }

    @Override
    public Transaction getById(int id) {
        return transactionRepository.findById(id).get();
    }

    @Override
    public Transaction create(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction update(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public void delete(int id) {
        transactionRepository.deleteById(id);
    }
}
