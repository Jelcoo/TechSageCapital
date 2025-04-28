package com.techsage.banking.models.info;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.techsage.banking.models.User;
import com.techsage.banking.models.enums.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserInfo {
    private Long id;
    private String firstName;
    private String lastName;

    @JsonIgnore
    private String email;

    @JsonIgnore
    private String phoneNumber;

    @JsonIgnore
    private Integer bsn;

    @JsonIgnore
    private String passwordHash;

    @JsonIgnore
    private String passwordSalt;

    @JsonIgnore
    private UserRole role;
    private Double dailyLimit;
    private Double transferLimit;

    @JsonIgnore
    private LocalDateTime createdAt;

    @JsonIgnore
    private String refreshToken;

    @JsonIgnore
    private LocalDateTime refreshTokenCreatedAt;

    @JsonIgnore
    private UserStatus status;
}
