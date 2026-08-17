package com.expense.expensesplitter.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        // At least 8 characters, 1 uppercase, 1 lowercase, 1 digit
        return password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$");
    }
}