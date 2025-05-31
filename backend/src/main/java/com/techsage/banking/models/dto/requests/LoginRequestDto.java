package com.techsage.banking.models.dto.requests;

import com.fasterxml.jackson.annotation.*;
import com.techsage.banking.models.dto.*;
import com.techsage.banking.models.enums.*;
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

    @NotNull(message = "Authentication scope is required")
    @JsonProperty("scope")
    private AuthenticationScope authenticationScope;

    public void setAuthenticationScope(String scope) {
        if (scope == null) {
            return;
        }
        this.authenticationScope = AuthenticationScope.valueOf(scope);
    }
}
