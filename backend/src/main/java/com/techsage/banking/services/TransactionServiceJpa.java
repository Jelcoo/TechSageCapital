package com.techsage.banking.services;

import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.Transaction;
import com.techsage.banking.models.dto.BankAccountDto;
import com.techsage.banking.models.dto.TransactionDto;
import com.techsage.banking.repositories.TransactionRepository;
import com.techsage.banking.services.interfaces.TransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceJpa implements TransactionService {
    private final TransactionRepository transactionRepository;
    ModelMapper modelMapper;

    public TransactionServiceJpa(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public List<TransactionDto> getAll() {
        List<Transaction> transactions = (List<Transaction>)transactionRepository.findAll();
        return transactions.stream().map(transaction -> modelMapper.map(transaction, TransactionDto.class)).toList();
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
