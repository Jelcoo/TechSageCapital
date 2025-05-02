package com.techsage.banking.validators;

import jakarta.validation.*;

public class BSNValidator implements ConstraintValidator<BSN, String> {

    @Override
    public void initialize(BSN constraintAnnotation) { }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // use @NotNull for null‑checks
        }
        // Must be exactly 9 digits
        if (!value.matches("\\d{9}")) {
            return false;
        }
        int sum = 0;
        int[] weights = {9,8,7,6,5,4,3,2,-1};
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(value.charAt(i)) * weights[i];
        }
        // sum must be nonzero multiple of 11
        return sum % 11 == 0 && sum != 0;
    }
}

