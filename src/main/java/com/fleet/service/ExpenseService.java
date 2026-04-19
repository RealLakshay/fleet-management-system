package com.fleet.service;

import com.fleet.dto.request.CreateExpenseRequest;
import com.fleet.dto.response.ExpenseResponse;

public interface ExpenseService {
    /**
     * Creates a new expense after verifying the referenced vehicle exists.
     */
    ExpenseResponse createExpense(CreateExpenseRequest request);

    /**
     * Returns one expense by id.
     */
    ExpenseResponse getExpenseById(String expenseId);

    /**
     * Updates an existing expense.
     */
    ExpenseResponse updateExpense(String expenseId, CreateExpenseRequest request);

    /**
     * Deletes an expense by id.
     */
    void deleteExpense(String expenseId);
}
