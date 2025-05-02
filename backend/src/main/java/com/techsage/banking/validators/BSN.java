package com.techsage.banking.validators;

import jakarta.validation.*;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BSNValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface BSN {
    String message() default "Invalid BSN";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
