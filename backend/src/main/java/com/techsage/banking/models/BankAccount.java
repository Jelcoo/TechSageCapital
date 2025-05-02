package com.techsage.banking.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.techsage.banking.converters.IbanConverter;
import com.techsage.banking.models.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.iban4j.Iban;

import java.util.List;

@Data
@AllArgsConstructor
@Entity
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    private User user;

    @Convert(converter = IbanConverter.class)
    private Iban iban;

    private Double balance;
    private int absoluteMinimumBalance;

    @Enumerated(EnumType.STRING)
    private BankAccountType type;

    @OneToMany(mappedBy = "fromAccount")
    private List<Transaction> outgoingTransactions;

    @OneToMany(mappedBy = "toAccount")
    private List<Transaction> incomingTransactions;

    public BankAccount() {}
}
