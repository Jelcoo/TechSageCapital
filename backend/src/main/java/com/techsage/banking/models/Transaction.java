package com.techsage.banking.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Entity
public class Transaction {
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "from_account_id", nullable = true)
    private BankAccount fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id", nullable = true)
    private BankAccount toAccount;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiatorId; // user id who initiated the transaction

    private Double amount;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String description;

    public enum Type {
        DEPOSIT,
        WITHDRAWAL,
        ATM_DEPOSIT,
        ATM_WITHDRAWAL
    }
    public Transaction() {}
}
