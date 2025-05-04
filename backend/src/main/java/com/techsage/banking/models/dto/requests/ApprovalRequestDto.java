package com.techsage.banking.models.dto.requests;

import lombok.Data;

@Data
public class ApprovalRequestDto {
    private Double transferLimit;
    private Double dailyTransferLimit;
}
