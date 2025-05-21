package com.techsage.banking.services.interfaces;

import com.techsage.banking.exceptions.TransactionException;
import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.TransactionDto;
import com.techsage.banking.models.dto.requests.TransactionRequestDto;

import java.math.*;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    List<TransactionDto> getAll();
    List<TransactionDto> getByAccountId(long id);
    List<TransactionDto> getByAccountIdAndCustomer(long id, String email);
    TransactionDto create(TransactionRequestDto transaction, User user) throws TransactionException;
    BigDecimal findSumOfTransactionsByFromAccount(BankAccount bankAccount, LocalDateTime date);
}
