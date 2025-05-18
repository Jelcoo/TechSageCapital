package com.techsage.banking.validators;

import jakarta.validation.*;

public class BSNValidator implements ConstraintValidator<BSN, String> {

    @Override
    public void initialize(BSN constraintAnnotation) { }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || !value.matches("\\d{9}")) {
            return false;
        }

        int total = 0;
        for (int i = 0; i < 8; i++) {
            total += Character.getNumericValue(value.charAt(i)) * (9 - i);
        }

        total -= Character.getNumericValue(value.charAt(8));

        return total % 11 == 0;
    }
}

