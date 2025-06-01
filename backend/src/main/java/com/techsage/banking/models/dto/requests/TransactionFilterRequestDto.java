package com.techsage.banking.models.dto.requests;

import com.techsage.banking.models.enums.AmountFilterType;
import lombok.Data;

@Data
public class TransactionFilterRequestDto {
    private String startDate;
    private String endDate;
    private AmountFilterType amountFilterType;
    private String amountFilterValue;

    @com.techsage.banking.validators.Iban
    private String ibanFilter;
}
