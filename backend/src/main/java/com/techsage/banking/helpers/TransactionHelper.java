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

import java.time.LocalDateTime;

@Component
public class TransactionHelper {
    private final TransactionService transactionService;

    public TransactionHelper(@Lazy TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    private boolean CheckDailyLimit(BankAccount fromBankAccount, BankAccount toBankAccount, Double amount) {
        if (fromBankAccount.getType() == BankAccountType.SAVINGS || toBankAccount.getType() == BankAccountType.SAVINGS) {
            return true;
        }
        Double sum = transactionService.findSumOfTransactionsByFromAccount(fromBankAccount, LocalDateTime.now().plusHours(24));
        return sum + amount <= fromBankAccount.getUser().getDailyLimit();
    }

    private boolean CheckTransferLimit(BankAccount fromBankAccount, BankAccount toBankAccount, Double amount) {
        if (fromBankAccount.getType() == BankAccountType.SAVINGS || toBankAccount.getType() == BankAccountType.SAVINGS) {
            return true;
        }
        return amount <= fromBankAccount.getUser().getTransferLimit();
    }

    private boolean CheckWithdrawalLimit(BankAccount fromBankAccount, Double amount) {
        return amount - fromBankAccount.getBalance() <= fromBankAccount.getAbsoluteMinimumBalance();
    }

    private boolean CheckOwnSavingsAccount(BankAccount fromBankAccount, BankAccount toBankAccount) {
        if (fromBankAccount.getType() == BankAccountType.SAVINGS) {
            return fromBankAccount.getUser().getId().equals(toBankAccount.getUser().getId());
        } else {
            return true;
        }
    }

    private boolean CheckOwnership(BankAccount fromBankAccount, User initiator) {
        if (initiator.getRoles().contains(UserRole.ROLE_ADMIN) || initiator.getRoles().contains(UserRole.ROLE_EMPLOYEE)) {
            return true;
        }
        return fromBankAccount.getUser().getId().equals(initiator.getId());
    }
    
    public boolean ValidateTransaction(BankAccount fromAccount, BankAccount toAccount, User initiator, Double amount) throws TransactionException {
        if (fromAccount == null || toAccount == null) {
            throw new TransactionException(TransactionException.Reason.BANK_ACCOUNT_NOT_FOUND);
        }
        if (!this.CheckOwnership(fromAccount, initiator)) {
            throw new TransactionException(TransactionException.Reason.CHECK_OWNERSHIP);
        }
        if (!this.CheckOwnSavingsAccount(fromAccount, toAccount)) {
            throw new TransactionException(TransactionException.Reason.CHECK_OWN_SAVINGS_ACCOUNT);
        }
        if (!this.CheckWithdrawalLimit(fromAccount, amount)) {
            throw new TransactionException(TransactionException.Reason.CHECK_WITHDRAWAL_LIMIT);
        }
        if (!this.CheckTransferLimit(fromAccount, toAccount, amount)) {
            throw new TransactionException(TransactionException.Reason.CHECK_TRANSFER_LIMIT);
        }
        if (!this.CheckDailyLimit(fromAccount, toAccount, amount)) {
            throw new TransactionException(TransactionException.Reason.CHECK_DAILY_LIMIT);
        }
        return true;
    }
}
