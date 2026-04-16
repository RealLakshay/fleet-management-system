package com.fleet.service;

import com.fleet.dto.request.CreateExpenseRequest;
import com.fleet.dto.response.ExpenseResponse;

public interface ExpenseService {
    ExpenseResponse createExpense(CreateExpenseRequest request);

    ExpenseResponse getExpenseById(String expenseId);

    ExpenseResponse updateExpense(String expenseId, CreateExpenseRequest request);

    void deleteExpense(String expenseId);
}