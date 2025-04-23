package com.techsage.banking.models.info;

import com.techsage.banking.models.Transaction;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionInfo {
    private Long id;
    private BankAccountInfo fromAccount;
//    private BankAccountInfo toAccount;
    private UserInfo initiator; // user id who initiated the transaction
    private Double amount;
    private LocalDateTime createdAt;
    private Transaction.Type type;
    private String description;
}
