package com.techsage.banking.models.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateSelfRequestDto
{
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Phone number is required")
    private String phoneNumber;

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }
}
