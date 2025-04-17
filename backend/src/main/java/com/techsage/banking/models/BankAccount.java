package com.techsage.banking.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.techsage.banking.converters.IbanConverter;
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
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Convert(converter = IbanConverter.class)
    @JsonIgnore
    private Iban iban;

    private Double balance;
    private int absoluteMinimumBalance;

    @Enumerated(EnumType.STRING)
    private Type type;

    @OneToMany(mappedBy = "fromAccount", fetch = FetchType.LAZY)
    private List<Transaction> outgoingTransactions;

    @OneToMany(mappedBy = "toAccount", fetch = FetchType.LAZY)
    private List<Transaction> incomingTransactions;

    public enum Type {
        SAVINGS,
        CHECKING
    }

    public BankAccount() {}
}
