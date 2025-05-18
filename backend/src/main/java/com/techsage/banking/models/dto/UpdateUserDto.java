package com.techsage.banking.models.dto;

import com.techsage.banking.models.enums.*;
import com.techsage.banking.models.info.BankAccountInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class UpdateUserDto extends BaseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Integer bsn;

    private List<UserRole> roles;

    private Double dailyLimit;
    private Double transferLimit;
    private LocalDateTime createdAt;
    private String refreshToken;
    private LocalDateTime refreshTokenCreatedAt;
    private UserStatus status;

    private List<BankAccountInfo> bankAccounts;
}
