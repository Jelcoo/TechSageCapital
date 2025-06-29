package com.techsage.banking.models.dto.requests;

import com.techsage.banking.validators.FieldsMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@FieldsMatch(first = "newPassword", second = "confirmNewPassword", message = "Passwords must match")
public class UpdatePasswordRequestDto {
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_])\\S{8,}$",
            message = "Password must be at least 8 characters, contain at least one digit, one lowercase letter, one uppercase letter, and one special character, and have no whitespace")
    private String newPassword;

    @NotBlank(message = "Confirm new password is required")
    private String confirmNewPassword;
}
