package com.techsage.banking.models;

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
    @GeneratedValue
    private int id;

    @ManyToOne
    private User user;

    @Convert(converter = IbanConverter.class)
    private Iban iban;

    private Double balance;
    private int absoluteMinimumBalance;

    @Enumerated(EnumType.STRING)
    private Type type;

    @OneToMany(mappedBy = "fromAccount")
    private List<Transaction> outgoingTransactions;

    @OneToMany(mappedBy = "toAccount")
    private List<Transaction> incomingTransactions;

    public enum Type {
        SAVINGS,
        CHECKING
    }

    public BankAccount() {}
}
