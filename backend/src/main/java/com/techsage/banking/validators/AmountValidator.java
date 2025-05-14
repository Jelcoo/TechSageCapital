package com.techsage.banking.validators;

import jakarta.validation.*;

import java.math.*;

public class AmountValidator implements ConstraintValidator<ValidAmount, BigDecimal> {
    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return value.scale() == 2;
    }
}
