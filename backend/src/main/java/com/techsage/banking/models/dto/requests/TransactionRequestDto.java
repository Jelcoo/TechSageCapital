package com.techsage.banking.models.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iban4j.Iban;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDto {
    private Iban fromIban;
    private Iban toIban;
    private Double amount;
    private String description;
}
