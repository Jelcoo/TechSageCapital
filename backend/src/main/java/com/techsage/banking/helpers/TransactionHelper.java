package com.techsage.banking.helpers;

import com.techsage.banking.exceptions.TransactionException;
import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.Transaction;
import com.techsage.banking.models.User;
import com.techsage.banking.models.enums.BankAccountType;
import com.techsage.banking.models.enums.TransactionType;
import com.techsage.banking.models.enums.UserRole;
import com.techsage.banking.services.interfaces.BankAccountService;
import com.techsage.banking.services.interfaces.TransactionService;
import org.iban4j.Iban;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.math.*;
import java.time.LocalDateTime;

@Component
public class TransactionHelper {
    private final TransactionService transactionService;

    public TransactionHelper(@Lazy TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    private boolean checkDailyLimit(BankAccount fromBankAccount, BankAccount toBankAccount, BigDecimal amount) {
        if (fromBankAccount.getType() == BankAccountType.SAVINGS || toBankAccount.getType() == BankAccountType.SAVINGS) {
            return true;
        }

        BigDecimal sum = transactionService.findSumOfTransactionsByFromAccount(fromBankAccount, LocalDateTime.now().plusHours(24));

        BigDecimal total = sum.add(amount);
        BigDecimal dailyLimit = fromBankAccount.getUser().getDailyLimit();

        return total.compareTo(dailyLimit) <= 0;
    }

    private boolean checkTransferLimit(BankAccount fromBankAccount, BankAccount toBankAccount, BigDecimal amount) {
        if (fromBankAccount.getType() == BankAccountType.SAVINGS || toBankAccount.getType() == BankAccountType.SAVINGS) {
            return true;
        }

        BigDecimal transferLimit = fromBankAccount.getUser().getTransferLimit();
        return amount.compareTo(transferLimit) <= 0;
    }

    private boolean checkWithdrawalLimit(BankAccount fromBankAccount, BigDecimal amount) {
        BigDecimal balanceAfterWithdrawal = fromBankAccount.getBalance().subtract(amount);
        BigDecimal absoluteMin = fromBankAccount.getAbsoluteMinimumBalance();

        return balanceAfterWithdrawal.compareTo(absoluteMin) >= 0;
    }
    
    private boolean checkOwnSavingsAccount(BankAccount fromBankAccount, BankAccount toBankAccount) {
        if (fromBankAccount.getType() == BankAccountType.SAVINGS) {
            return fromBankAccount.getUser().getId().equals(toBankAccount.getUser().getId());
        } else {
            return true;
        }
    }

    private boolean checkOwnership(BankAccount fromBankAccount, User initiator) {
        if (initiator.getRoles().contains(UserRole.ROLE_ADMIN) || initiator.getRoles().contains(UserRole.ROLE_EMPLOYEE)) {
            return true;
        }
        return fromBankAccount.getUser().getId().equals(initiator.getId());
    }
    
    public boolean validateTransaction(BankAccount fromAccount, BankAccount toAccount, User initiator, BigDecimal amount) throws TransactionException {
        if (fromAccount == null || toAccount == null) {
            throw new TransactionException(TransactionException.Reason.BANK_ACCOUNT_NOT_FOUND);
        }
        if (!this.checkOwnership(fromAccount, initiator)) {
            throw new TransactionException(TransactionException.Reason.CHECK_OWNERSHIP);
        }
        if (!this.checkOwnSavingsAccount(fromAccount, toAccount)) {
            throw new TransactionException(TransactionException.Reason.CHECK_OWN_SAVINGS_ACCOUNT);
        }
        if (!this.checkWithdrawalLimit(fromAccount, amount)) {
            throw new TransactionException(TransactionException.Reason.CHECK_WITHDRAWAL_LIMIT);
        }
        if (!this.checkTransferLimit(fromAccount, toAccount, amount)) {
            throw new TransactionException(TransactionException.Reason.CHECK_TRANSFER_LIMIT);
        }
        if (!this.checkDailyLimit(fromAccount, toAccount, amount)) {
            throw new TransactionException(TransactionException.Reason.CHECK_DAILY_LIMIT);
        }
        return true;
    }

    public boolean validateAtmDeposit(BankAccount toAccount, User initiator) throws TransactionException {
        if (toAccount == null) {
            throw new TransactionException(TransactionException.Reason.BANK_ACCOUNT_NOT_FOUND);
        }
        if (!this.checkOwnership(toAccount, initiator)) {
            throw new TransactionException(TransactionException.Reason.CHECK_OWNERSHIP);
        }
        return true;
    }

    public boolean validateAtmWithdrawal(BankAccount fromAccount, User initiator, BigDecimal amount) throws TransactionException {
        if (fromAccount == null) {
            throw new TransactionException(TransactionException.Reason.BANK_ACCOUNT_NOT_FOUND);
        }
        if (!this.checkOwnership(fromAccount, initiator)) {
            throw new TransactionException(TransactionException.Reason.CHECK_OWNERSHIP);
        }
        if (!this.checkWithdrawalLimit(fromAccount, amount)) {
            throw new TransactionException(TransactionException.Reason.CHECK_WITHDRAWAL_LIMIT);
        }
        return true;
    }
}
