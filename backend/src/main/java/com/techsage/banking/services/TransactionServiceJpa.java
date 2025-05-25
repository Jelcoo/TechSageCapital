package com.techsage.banking.services;

import com.techsage.banking.exceptions.TransactionException;
import com.techsage.banking.helpers.TransactionHelper;
import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.Transaction;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.*;
import com.techsage.banking.models.dto.requests.TransactionRequestDto;
import com.techsage.banking.models.enums.TransactionType;
import com.techsage.banking.models.info.BankAccountInfoWithoutBalance;
import com.techsage.banking.repositories.TransactionRepository;
import com.techsage.banking.services.interfaces.BankAccountService;
import com.techsage.banking.services.interfaces.TransactionService;
import com.techsage.banking.services.interfaces.UserService;
import jakarta.transaction.Transactional;
import org.iban4j.Iban;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TransactionServiceJpa implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;
    private final TransactionHelper transactionHelper;
    private final BankAccountService bankAccountService;
    private final UserService userService;

    public TransactionServiceJpa(TransactionRepository transactionRepository, TransactionHelper transactionHelper, BankAccountService bankAccountService, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.transactionHelper = transactionHelper;
        this.bankAccountService = bankAccountService;
        this.userService = userService;
        this.modelMapper = new ModelMapper();
        modelMapper.typeMap(BankAccount.class, BankAccountInfoWithoutBalance.class).addMappings(mapper -> {
            mapper.map(src -> src.getUser().getFirstName(), BankAccountInfoWithoutBalance::setFirstName);
            mapper.map(src -> src.getUser().getLastName(), BankAccountInfoWithoutBalance::setLastName);
            mapper.map(src -> src.getIban() == null ? "" : src.getIban().getAccountNumber(), BankAccountInfoWithoutBalance::setIban);
            mapper.map(BankAccount::getType, BankAccountInfoWithoutBalance::setType);
        });
    }

    @Override
    public Page<TransactionDto> getAll(Pageable pageable) {
        Page<Transaction> transactionsPage = transactionRepository.findAllByOrderByCreatedAtDesc(pageable);
        return transactionsPage.map(transaction -> modelMapper.map(transaction, TransactionDto.class));
    }

    @Override
    public Page<TransactionDto> getByAccountId(long id, Pageable pageable) {
        BankAccount bankAccount = bankAccountService.getById(id);
        if (!transactionRepository.existsById(id)) {
            throw new TransactionException(TransactionException.Reason.BANK_ACCOUNT_NOT_FOUND);
        }
        Page<Transaction> transactionsPage = transactionRepository.findAllByBankAccount(bankAccount, pageable);
        return transactionsPage.map(transaction -> modelMapper.map(transaction, TransactionDto.class));
    }

    @Override
    public Page<TransactionDto> getByAccountIdAndCustomer(long id, String email, Pageable pageable) {
        User user = userService.getByEmailRaw(email);
        BankAccount bankAccount = bankAccountService.getById(id);
        if (bankAccount.getUser() != user) {
            throw new TransactionException(TransactionException.Reason.BANK_ACCOUNT_NOT_FOUND);
        }
        return getByAccountId(id, pageable);
    }

    @Override
    public TransactionDto create(TransactionRequestDto transaction, User initiator) throws TransactionException {
        BankAccount fromAccount = bankAccountService.getByIban(Iban.valueOf(transaction.getFromIban()));
        BankAccount toAccount = bankAccountService.getByIban(Iban.valueOf(transaction.getToIban()));
        BigDecimal amount = transaction.getAmount();

        try {
            transactionHelper.validateTransaction(fromAccount, toAccount, initiator, amount);
        } catch (TransactionException e) {
            throw new TransactionException(e.getReason());
        }

        Transaction fromTransaction = new Transaction(null, fromAccount, toAccount, initiator, amount, LocalDateTime.now(), TransactionType.WITHDRAWAL, transaction.getDescription());
        Transaction toTransaction = new Transaction(null, fromAccount, toAccount, initiator, amount, LocalDateTime.now(), TransactionType.DEPOSIT, transaction.getDescription());
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        try {
            bankAccountService.update(fromAccount);
            bankAccountService.update(toAccount);
            transactionRepository.save(toTransaction);
            return modelMapper.map(transactionRepository.save(fromTransaction), TransactionDto.class);
        } catch (Exception e) {
            throw new TransactionException(TransactionException.Reason.TRANSACTION_FAILED);
        }
    }

    @Override
    public BigDecimal findSumOfTransactionsByFromAccount(BankAccount bankAccount, LocalDateTime date) {
        return transactionRepository.findSumOfTransactionsByFromAccount(bankAccount, date);
    }
}
