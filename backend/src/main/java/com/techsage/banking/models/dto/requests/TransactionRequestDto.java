package com.techsage.banking.models.dto.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.techsage.banking.validators.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iban4j.Iban;

import java.math.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDto {
    @Schema(type = "string",
            format = "iban",
            example = "NLxxTESCxxxxxxxxxx")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @com.techsage.banking.validators.Iban
    private String fromIban;

    @Schema(type = "string",
            format = "iban",
            example = "NLxxTESCxxxxxxxxxx")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @com.techsage.banking.validators.Iban
    private String toIban;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    @ValidAmount
    private BigDecimal amount;

    private String description;
}
