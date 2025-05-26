package com.techsage.banking.models.dto.requests;

import com.techsage.banking.validators.DailyLimit;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.*;

@Data
@DailyLimit(first = "dailyTransferLimit", second = "transferLimit")
public class ApprovalRequestDto {
    @NotNull(message = "Transfer limit is required")
    @Min(value = 0, message = "Transfer limit must be at least 0")
    private BigDecimal transferLimit;

    @NotNull(message = "Daily transfer limit is required")
    @Min(value = 0, message = "Daily transfer limit must be at least 0")
    private BigDecimal dailyTransferLimit;

    @NotNull(message = "An absolute limit for checking account is required")
    private BigDecimal absoluteLimitChecking;

    @NotNull(message = "An absolute limit for savings account is required")
    private BigDecimal absoluteLimitSavings;

}
