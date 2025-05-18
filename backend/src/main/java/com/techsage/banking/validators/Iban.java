package com.techsage.banking.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IbanValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Iban {
    String message() default "Invalid Iban";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
