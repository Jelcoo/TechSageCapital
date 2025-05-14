package com.techsage.banking.validators;

import jakarta.validation.*;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AmountValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAmount {
    String message() default "Amount must have exactly two decimal places";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
