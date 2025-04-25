package com.techsage.banking.services.interfaces;

import com.techsage.banking.models.Transaction;
import com.techsage.banking.models.dto.TransactionDto;

import java.util.List;

public interface TransactionService {
    List<TransactionDto> getAll();
    Transaction getById(long id);
    Transaction create(Transaction transaction);
    Transaction update(Transaction transaction);
    void delete(long id);
}
