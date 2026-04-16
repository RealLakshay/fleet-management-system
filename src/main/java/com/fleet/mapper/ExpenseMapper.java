package com.fleet.mapper;

// Pure Fabrication: this mapper concentrates DTO/entity conversion in one place.
import com.fleet.dto.request.CreateExpenseRequest;
import com.fleet.dto.response.ExpenseResponse;
import com.fleet.model.Expense;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ExpenseMapper {
    public Expense toEntity(CreateExpenseRequest request) {
        Expense expense = new Expense();
        expense.setExpenseId(UUID.randomUUID().toString());
        updateFromRequest(request, expense);
        return expense;
    }

    public ExpenseResponse toResponse(Expense expense) {
        ExpenseResponse response = new ExpenseResponse();
        response.setExpenseId(expense.getExpenseId());
        response.setVehicleId(expense.getVehicleId());
        response.setExpenseType(expense.getExpenseType());
        response.setAmount(expense.getAmount());
        response.setExpenseDate(expense.getExpenseDate());
        response.setDescription(expense.getDescription());
        response.setReceiptNumber(expense.getReceiptNumber());
        response.setVendor(expense.getVendor());
        return response;
    }

    public void updateFromRequest(CreateExpenseRequest request, Expense expense) {
        if (request.getVehicleId() != null) expense.setVehicleId(request.getVehicleId());
        if (request.getExpenseType() != null) expense.setExpenseType(request.getExpenseType());
        if (request.getAmount() != null) expense.setAmount(request.getAmount());
        if (request.getExpenseDate() != null) expense.setExpenseDate(request.getExpenseDate());
        if (request.getDescription() != null) expense.setDescription(request.getDescription());
        if (request.getReceiptNumber() != null) expense.setReceiptNumber(request.getReceiptNumber());
        if (request.getVendor() != null) expense.setVendor(request.getVendor());
    }
}