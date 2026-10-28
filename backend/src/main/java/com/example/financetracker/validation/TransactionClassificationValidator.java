
package com.example.financetracker.validation;

import com.example.financetracker.dto.TransactionRequest;
import com.example.financetracker.entity.TransactionType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TransactionClassificationValidator
        implements ConstraintValidator<ValidTransactionClassification, TransactionRequest> {

    @Override
    public boolean isValid(
            TransactionRequest request,
            ConstraintValidatorContext context
    ) {
        if (request == null) {
            return true;
        }

        // Let @NotNull on the type field report a missing transaction type.
        if (request.getType() == null) {
            return true;
        }

        boolean hasExpenseCategory = request.getExpenseCategory() != null;
        boolean hasIncomeSource = request.getIncomeSource() != null;

        if (request.getType() == TransactionType.EXPENSE) {
            return hasExpenseCategory && !hasIncomeSource;
        }

        if (request.getType() == TransactionType.INCOME) {
            return hasIncomeSource && !hasExpenseCategory;
        }

        return false;
    }
}