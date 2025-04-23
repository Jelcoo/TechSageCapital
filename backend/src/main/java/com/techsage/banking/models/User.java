package com.techsage.banking.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String bsn;

    @JsonIgnore
    private String passwordHash;

    @JsonIgnore
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
    @JsonManagedReference
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
