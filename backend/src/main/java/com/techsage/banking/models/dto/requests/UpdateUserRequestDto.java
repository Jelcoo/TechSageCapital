package com.techsage.banking.models.dto.requests;

import com.techsage.banking.models.dto.BaseDto;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.models.info.BankAccountInfo;
import com.techsage.banking.validators.BSN;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class UpdateUserRequestDto extends BaseDto {
    @NotNull(message = "Firstname is required")
    private String firstName;

    @NotNull(message = "Lastname is required")
    private String lastName;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Phone number is required")
    private String phoneNumber;

    @NotNull(message = "BSN is required")
    @BSN
    private String bsn;

    @NotNull(message = "A role is required")
    private List<UserRole> roles;

    @NotNull(message = "Daily limit is required")
    private Double dailyLimit;

    @NotNull(message = "Transfer limit is required")
    private Double transferLimit;

    @NotNull(message = "Status is required")
    private UserStatus status;

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }
}
