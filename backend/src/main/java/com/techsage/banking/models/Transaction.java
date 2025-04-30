package com.techsage.banking.models;

import com.techsage.banking.models.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_account_id", nullable = true)
    private BankAccount fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id", nullable = true)
    private BankAccount toAccount;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator; // user id who initiated the transaction

    private Double amount;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String description;

    public Transaction() {}
}
