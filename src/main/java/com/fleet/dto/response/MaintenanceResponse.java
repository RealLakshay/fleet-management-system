/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MaintenanceResponse {
    private String maintenanceId;
    private String vehicleId;
    private LocalDate serviceDate;
    private String serviceType;
    private String description;
    private String serviceProvider;
    private BigDecimal cost;
    private LocalDate nextServiceDue;
    private Integer odometerReading;
}
