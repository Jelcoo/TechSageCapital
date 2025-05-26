package com.techsage.banking.models.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateAbsoluteMinimumBalanceRequestDto {
    @NotNull(message = "Absolute minimum balance is required")
    private BigDecimal absoluteMinimumBalance;
}
