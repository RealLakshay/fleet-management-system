package com.fleet.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReportResponse {
    private String period;
    private Long tripCount;
    private BigDecimal totalDistance;
    private BigDecimal totalExpense;
}