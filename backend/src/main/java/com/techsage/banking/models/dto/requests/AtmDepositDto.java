package com.techsage.banking.models.dto.requests;

import com.fasterxml.jackson.annotation.*;
import com.techsage.banking.models.dto.*;
import com.techsage.banking.validators.*;
import io.swagger.v3.oas.annotations.media.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.*;

@Data
public class AtmDepositDto extends BaseDto {
    @Schema(type = "string",
            format = "iban",
            example = "NLxxINHOxxxxxxxxxx")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @com.techsage.banking.validators.Iban
    private String depositTo;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    @ValidAmount
    private BigDecimal amount;

}
