package com.expense.expensesplitter.config;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CustomValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Password must be at least 8 characters with uppercase, lowercase and digit";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}