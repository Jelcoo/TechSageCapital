package com.techsage.banking.models.dto.requests;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.*;

@Data
public class ApprovalRequestDto {
    @NotNull(message = "Transfer limit is required")
    @Min(value = 0, message = "Transfer limit must be at least 0")
    @Max(value = 1_000_000, message = "Transfer limit must be below 1.000.000")
    private BigDecimal transferLimit;

    @NotNull(message = "Daily transfer limit is required")
    @Min(value = 0, message = "Daily transfer limit must be at least 0")
    @Max(value = 1_000_000, message = "Daily transfer limit must be below 1.000.000")
    private BigDecimal dailyTransferLimit;
}
