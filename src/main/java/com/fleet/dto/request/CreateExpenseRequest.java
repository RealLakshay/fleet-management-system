package com.fleet.dto.request;

import com.fleet.model.enums.ExpenseType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateExpenseRequest {
    private String vehicleId;
    private ExpenseType expenseType;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String description;
    private String receiptNumber;
    private String vendor;
}