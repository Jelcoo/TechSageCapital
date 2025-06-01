package com.techsage.banking.models.dto.requests;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateSelfRequestDto
{
    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "Phone number is required")
    private String phoneNumber;

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }
}
