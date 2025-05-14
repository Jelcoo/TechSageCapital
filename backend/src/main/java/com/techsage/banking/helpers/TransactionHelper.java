package com.techsage.banking.helpers;

import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.Transaction;
import com.techsage.banking.models.User;
import com.techsage.banking.models.enums.BankAccountType;
import com.techsage.banking.models.enums.TransactionType;
import com.techsage.banking.services.interfaces.BankAccountService;
import com.techsage.banking.services.interfaces.TransactionService;
import org.iban4j.Iban;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionHelper {
    private final TransactionService transactionService;
    private final BankAccountService bankAccountService;

    public TransactionHelper(@Lazy TransactionService transactionService, BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
        this.transactionService = transactionService;
    }

    public boolean CheckDailyLimit(BankAccount fromBankAccount, BankAccount toBankAccount, Double amount) {
        if (fromBankAccount.getType() == BankAccountType.SAVINGS || toBankAccount.getType() == BankAccountType.SAVINGS) {
            return true;
        }
        Double sum = transactionService.findSumOfTransactionsByFromAccount(fromBankAccount, LocalDateTime.now().plusHours(24));
        return sum + amount <= fromBankAccount.getUser().getDailyLimit();
    }

    public boolean CheckTransferLimit(BankAccount fromBankAccount, BankAccount toBankAccount, Double amount) {
        if (fromBankAccount.getType() == BankAccountType.SAVINGS || toBankAccount.getType() == BankAccountType.SAVINGS) {
            return true;
        }
        return amount <= fromBankAccount.getUser().getTransferLimit();
    }

    public boolean CheckWithdrawalLimit(BankAccount fromBankAccount, Double amount) {
        return amount - fromBankAccount.getBalance() <= fromBankAccount.getAbsoluteMinimumBalance();
    }

    public boolean CheckBalance(BankAccount fromBankAccount, Double amount) {
        // TODO: check if user absolute minimum balance
        return fromBankAccount.getBalance() >= amount;
    }
}
