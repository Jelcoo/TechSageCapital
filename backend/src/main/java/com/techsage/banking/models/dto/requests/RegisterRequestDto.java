package com.techsage.banking.models.dto.requests;

import com.fasterxml.jackson.annotation.*;
import com.techsage.banking.models.dto.*;
import com.techsage.banking.validators.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@FieldsMatch(first = "password", second = "confirmPassword", message = "Passwords must match")
public class RegisterRequestDto extends BaseDto {
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "BSN is required")
    @BSN
    private String bsn;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", 
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "Please finish the Turnstile challenge")
    @JsonProperty("cf-turnstile-response")
    private String cfTurnstileResponse;
}
