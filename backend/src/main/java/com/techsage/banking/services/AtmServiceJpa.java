package com.techsage.banking.services;

import com.techsage.banking.exceptions.*;
import com.techsage.banking.helpers.*;
import com.techsage.banking.jwt.*;
import com.techsage.banking.models.*;
import com.techsage.banking.models.Transaction;
import com.techsage.banking.models.dto.*;
import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.models.dto.responses.*;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.repositories.*;
import com.techsage.banking.services.interfaces.*;
import jakarta.transaction.*;
import org.iban4j.*;
import org.modelmapper.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.stereotype.*;

import javax.naming.*;
import java.time.*;
import java.util.*;

@Service
@Transactional
public class AtmServiceJpa implements AtmService {
    private final BankAccountService bankAccountService;
    private final TransactionRepository transactionRepository;
    private final TransactionHelper transactionHelper;
    private final ModelMapper modelMapper = new ModelMapper();

    public AtmServiceJpa(BankAccountService bankAccountService, TransactionRepository transactionRepository, TransactionHelper transactionHelper) {
        this.bankAccountService = bankAccountService;
        this.transactionRepository = transactionRepository;
        this.transactionHelper = transactionHelper;
    }

    @Override
    public BankAccountDto deposit(AtmDepositDto atmDepositDto, User initiator) throws TransactionException {
        BankAccount toAccount = bankAccountService.getByIban(Iban.valueOf(atmDepositDto.getDepositTo()));
        double amount = atmDepositDto.getAmount();

        try {
            transactionHelper.ValidateAtmDeposit(toAccount, initiator);
        } catch (TransactionException e) {
            throw new TransactionException(e.getReason());
        }

        Transaction transaction = new Transaction(null, null, toAccount, initiator, amount, LocalDateTime.now(), TransactionType.ATM_DEPOSIT, "ATM Deposit");
        toAccount.setBalance(toAccount.getBalance() + amount);

        try {
            bankAccountService.update(toAccount);
            transactionRepository.save(transaction);
            return modelMapper.map(toAccount, BankAccountDto.class);
        } catch (Exception e) {
            throw new TransactionException(TransactionException.Reason.TRANSACTION_FAILED);
        }
    }

    @Override
    public BankAccountDto withdraw(AtmWithdrawDto atmWithdrawDto, User initiator) throws TransactionException {
        BankAccount fromAccount = bankAccountService.getByIban(Iban.valueOf(atmWithdrawDto.getWithdrawFrom()));
        double amount = atmWithdrawDto.getAmount();

        try {
            transactionHelper.ValidateAtmWithdrawal(fromAccount, initiator, amount);
        } catch (TransactionException e) {
            throw new TransactionException(e.getReason());
        }

        Transaction transaction = new Transaction(null, fromAccount, null, initiator, amount, LocalDateTime.now(), TransactionType.ATM_WITHDRAWAL, "ATM Withdrawal");
        fromAccount.setBalance(fromAccount.getBalance() - amount);

        try {
            bankAccountService.update(fromAccount);
            transactionRepository.save(transaction);
            return modelMapper.map(fromAccount, BankAccountDto.class);
        } catch (Exception e) {
            throw new TransactionException(TransactionException.Reason.TRANSACTION_FAILED);
        }
    }
}
