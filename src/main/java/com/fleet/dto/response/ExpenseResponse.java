/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.dto.response;

import com.fleet.model.enums.ExpenseType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseResponse {
    private String expenseId;
    private String vehicleId;
    private ExpenseType expenseType;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String description;
    private String receiptNumber;
    private String vendor;
}
