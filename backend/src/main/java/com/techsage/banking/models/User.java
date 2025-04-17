package com.techsage.banking.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue
    private int id;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String bsn;
    private String passwordHash;
    private String passwordSalt;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Double dailyLimit;
    private Double transferLimit;
    private LocalDateTime createdAt;
    private String refreshToken;
    private LocalDateTime refreshTokenCreatedAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "user")
    private List<BankAccount> bankAccounts;

    public enum Role {
        CUSTOMER,
        EMPLOYEE,
        ADMIN
    }

    public enum Status {
        PENDING,
        ACTIVE,
        DELETED
    }

    public User() {}
}
