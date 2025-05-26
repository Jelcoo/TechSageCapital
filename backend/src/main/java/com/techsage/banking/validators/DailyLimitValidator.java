package com.techsage.banking.validators;

import com.techsage.banking.models.dto.requests.ApprovalRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Objects;

public class DailyLimitValidator implements ConstraintValidator<DailyLimit, Object> {

    private String firstFieldName;
    private String secondFieldName;
    private String message;

    @Override
    public void initialize(DailyLimit constraintAnnotation) {
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

            BigDecimal firstValue = (BigDecimal) firstField.get(value);
            BigDecimal secondValue = (BigDecimal) secondField.get(value);

            boolean valid = firstValue.compareTo(secondValue) >= 0;
            if (!valid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(firstFieldName) // associate the message with the second field
                        .addConstraintViolation();
            }
            return valid;

        } catch (Exception e) {
            return false;
        }
    }
}
