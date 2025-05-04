package com.techsage.banking.models.dto.requests;

import lombok.Data;

@Data
public class UserLimitsRequestDto {
    private Double transferLimit;
    private Double dailyTransferLimit;
}
