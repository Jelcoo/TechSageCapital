package com.techsage.banking.models.dto.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iban4j.Iban;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDto {
    @Schema(type = "string",
            format = "iban",
            example = "NLxxINHOxxxxxxxxxx")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @com.techsage.banking.validators.Iban
    private String fromIban;

    @Schema(type = "string",
            format = "iban",
            example = "NLxxINHOxxxxxxxxxx")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @com.techsage.banking.validators.Iban
    private String toIban;

    private Double amount;
    private String description;
}
