package com.techsage.banking.models.info;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.techsage.banking.models.BankAccount;
import com.techsage.banking.models.Transaction;
import com.techsage.banking.models.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionInfo {
    private Integer id;
    private BankAccountInfo fromAccount;
    private BankAccountInfo toAccount;
    private UserInfo initiatorId; // user id who initiated the transaction
    private Double amount;
    private LocalDateTime createdAt;
    private Transaction.Type type;
    private String description;
}
