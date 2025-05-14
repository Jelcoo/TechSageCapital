package com.techsage.banking.services;

import com.techsage.banking.exceptions.TransactionException;
import com.techsage.banking.helpers.TransactionHelper;
import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.Transaction;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.TransactionDto;
import com.techsage.banking.models.dto.requests.TransactionRequestDto;
import com.techsage.banking.models.enums.TransactionType;
import com.techsage.banking.models.info.BankAccountInfoWithoutBalance;
import com.techsage.banking.repositories.TransactionRepository;
import com.techsage.banking.services.interfaces.BankAccountService;
import com.techsage.banking.services.interfaces.TransactionService;
import jakarta.transaction.Transactional;
import org.iban4j.Iban;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TransactionServiceJpa implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;
    private final TransactionHelper transactionHelper;
    private final BankAccountService bankAccountService;

    public TransactionServiceJpa(TransactionRepository transactionRepository, TransactionHelper transactionHelper, BankAccountService bankAccountService) {
        this.transactionRepository = transactionRepository;
        this.transactionHelper = transactionHelper;
        this.bankAccountService = bankAccountService;
        this.modelMapper = new ModelMapper();
        modelMapper.typeMap(BankAccount.class, BankAccountInfoWithoutBalance.class).addMappings(mapper -> {
            mapper.map(src -> src.getUser().getFirstName(), BankAccountInfoWithoutBalance::setFirstName);
            mapper.map(src -> src.getUser().getLastName(), BankAccountInfoWithoutBalance::setLastName);
            mapper.map(src -> src.getIban() == null ? "" : src.getIban().getAccountNumber(), BankAccountInfoWithoutBalance::setIban);
        });
    }

    @Override
    public List<TransactionDto> getAll() {
        List<Transaction> transactions = (List<Transaction>)transactionRepository.findAll();
        return transactions.stream().map(transaction -> modelMapper.map(transaction, TransactionDto.class)).toList();
    }

    @Override
    public Transaction getById(long id) {
        return transactionRepository.findById(id).get();
    }

    @Override
    public TransactionDto create(TransactionRequestDto transaction, User user) throws TransactionException {
        BankAccount fromAccount = bankAccountService.getByIban(Iban.valueOf(transaction.getFromIban()));
        BankAccount toAccount = bankAccountService.getByIban(Iban.valueOf(transaction.getToIban()));
        double amount = transaction.getAmount();
        if (fromAccount == null || toAccount == null) {
            throw new TransactionException(TransactionException.Reason.BANK_ACCOUNT_NOT_FOUND);
        }
        if (!transactionHelper.CheckOwnSavingsAccount(fromAccount, toAccount)) {
            throw new TransactionException(TransactionException.Reason.CHECK_OWN_SAVINGS_ACCOUNT);
        }
        if (!transactionHelper.CheckWithdrawalLimit(fromAccount, amount)) {
            throw new TransactionException(TransactionException.Reason.CHECK_WITHDRAWAL_LIMIT);
        }
        if (!transactionHelper.CheckTransferLimit(fromAccount, toAccount, amount)) {
            throw new TransactionException(TransactionException.Reason.CHECK_TRANSFER_LIMIT);
        }
        if (!transactionHelper.CheckDailyLimit(fromAccount, toAccount, amount)) {
            throw new TransactionException(TransactionException.Reason.CHECK_DAILY_LIMIT);
        }
        Transaction fromTransaction = new Transaction(null, fromAccount, toAccount, user, amount, LocalDateTime.now(), TransactionType.WITHDRAWAL, transaction.getDescription());
        Transaction toTransaction = new Transaction(null, fromAccount, toAccount, user, amount, LocalDateTime.now(), TransactionType.DEPOSIT, transaction.getDescription());
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);
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
    public Double findSumOfTransactionsByFromAccount(BankAccount bankAccount, LocalDateTime date) {
        return transactionRepository.findSumOfTransactionsByFromAccount(bankAccount, date);
    }
}
