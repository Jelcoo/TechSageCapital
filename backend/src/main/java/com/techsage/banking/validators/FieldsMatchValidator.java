package com.techsage.banking.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class FieldsMatchValidator implements ConstraintValidator<FieldsMatch, Object> {

    private String firstFieldName;
    private String secondFieldName;
    private String message;

    @Override
    public void initialize(FieldsMatch constraintAnnotation) {
        this.firstFieldName = constraintAnnotation.first();
        this.secondFieldName = constraintAnnotation.second();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Field firstField = value.getClass().getDeclaredField(firstFieldName);
            Field secondField = value.getClass().getDeclaredField(secondFieldName);
            firstField.setAccessible(true);
            secondField.setAccessible(true);

            Object firstValue = firstField.get(value);
            Object secondValue = secondField.get(value);

            boolean valid = firstValue == null ? secondValue == null : firstValue.equals(secondValue);
            if (!valid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(secondFieldName) // associate the message with the second field
                        .addConstraintViolation();
            }
            return valid;

        } catch (Exception e) {
            return false;
        }
    }
}
