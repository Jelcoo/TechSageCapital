package com.techsage.banking.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.techsage.banking.models.enums.*;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

import java.math.*;
import java.time.LocalDateTime;
import java.util.*;

@Data
@NoArgsConstructor
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
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<UserRole> roles = List.of(UserRole.ROLE_USER, UserRole.ROLE_CUSTOMER);

    private BigDecimal dailyLimit = BigDecimal.valueOf(0.0);
    private BigDecimal transferLimit = BigDecimal.valueOf(0.0);

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(length = 1000)
    private String refreshToken;
    private LocalDateTime refreshTokenCreatedAt;

    @Setter
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.PENDING;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<BankAccount> bankAccounts;
}
