package com.techsage.banking.services.interfaces;

import com.techsage.banking.exceptions.TransactionException;
import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.TransactionDto;
import com.techsage.banking.models.dto.requests.AllTransactionFilterRequestDto;
import com.techsage.banking.models.dto.requests.TransactionFilterRequestDto;
import com.techsage.banking.models.dto.requests.TransactionRequestDto;
import org.springframework.data.domain.*;

import java.math.*;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    Page<TransactionDto> getAll(Pageable pageable, AllTransactionFilterRequestDto filter);
    Page<TransactionDto> getByAccountId(long id, Pageable pageable, TransactionFilterRequestDto filter);
    Page<TransactionDto> getByAccountIdAndCustomer(long id, String email, Pageable pageable, TransactionFilterRequestDto filter);
    TransactionDto create(TransactionRequestDto transaction, User user) throws TransactionException;
    BigDecimal findSumOfTransactionsByFromAccount(BankAccount bankAccount, LocalDateTime date);
}
