package com.techsage.banking.models.dto.requests;

import com.techsage.banking.models.dto.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class RefreshRequestDto extends BaseDto {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
