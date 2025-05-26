package com.techsage.banking.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DailyLimitValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DailyLimit {

    String message() default "Daily limit cannot be less than transfer limit";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // The first field to compare
    String first();

    // The second field to compare and report the error on
    String second();

    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        DailyLimit[] value();
    }
}

