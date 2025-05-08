package com.techsage.banking.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TurnstileValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Turnstile {
    String message() default "Invalid Turnstile response";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 