package com.techsage.banking.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.techsage.banking.models.User;
import com.techsage.banking.models.info.BankAccountInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Integer bsn;

    @JsonIgnore
    private String passwordHash;

    @JsonIgnore
    private String passwordSalt;

    private User.Role role;

    private Double dailyLimit;
    private Double transferLimit;
    private LocalDateTime createdAt;
    private String refreshToken;
    private LocalDateTime refreshTokenCreatedAt;
    private User.Status status;

    private List<BankAccountInfo> bankAccounts;
}
