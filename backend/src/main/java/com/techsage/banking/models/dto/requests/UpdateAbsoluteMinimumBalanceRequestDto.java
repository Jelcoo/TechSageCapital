package com.techsage.banking.models.dto.requests;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateAbsoluteMinimumBalanceRequestDto {
    private BigDecimal absoluteMinimumBalance;
}
