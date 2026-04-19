/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.model;

import com.fleet.model.enums.ExpenseType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "expenses")
public class Expense {
    @Id
    @Column(name = "expense_id")
    private String expenseId;

    @Column(name = "vehicle_id")
    private String vehicleId;

    @Enumerated(EnumType.STRING)
    private ExpenseType expenseType;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String description;
    private String receiptNumber;
    private String vendor;
}
