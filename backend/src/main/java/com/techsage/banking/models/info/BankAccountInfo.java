package com.techsage.banking.models.info;

import com.techsage.banking.models.BankAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iban4j.Iban;

import java.util.List;

@Data
@NoArgsConstructor
public class BankAccountInfo {
    private Integer id;
    private Iban iban;
    private Double balance;
    private int absoluteMinimumBalance;
    private BankAccount.Type type;
    private List<TransactionInfo> outgoingTransactions;
    private List<TransactionInfo> incomingTransactions;
}
