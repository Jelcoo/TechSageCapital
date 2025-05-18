package com.techsage.banking.models.dto.requests;

import com.fasterxml.jackson.annotation.*;
import com.techsage.banking.models.dto.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class LoginRequestDto extends BaseDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Please finish the Turnstile challenge")
    @JsonProperty("cf-turnstile-response")
    private String cfTurnstileResponse;

}
