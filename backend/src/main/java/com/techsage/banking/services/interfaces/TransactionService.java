package com.techsage.banking.services.interfaces;

import com.techsage.banking.exceptions.TransactionException;
import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.Transaction;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.TransactionDto;
import com.techsage.banking.models.dto.UserDto;
import com.techsage.banking.models.dto.requests.TransactionRequestDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    List<TransactionDto> getAll();
    Transaction getById(long id);
    TransactionDto create(TransactionRequestDto transaction, User user) throws TransactionException;
    Double findSumOfTransactionsByFromAccount(BankAccount bankAccount, LocalDateTime date);
}
