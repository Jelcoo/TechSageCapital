package com.techsage.banking.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.models.info.BankAccountInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class UserDto extends BaseDto {
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

    private List<UserRole> roles;

    private BigDecimal dailyLimit;
    private BigDecimal transferLimit;
    private LocalDateTime createdAt;

    @JsonIgnore
    private String refreshToken;

    @JsonIgnore
    private LocalDateTime refreshTokenCreatedAt;
    
    private UserStatus status;

    private List<BankAccountInfo> bankAccounts;
}
