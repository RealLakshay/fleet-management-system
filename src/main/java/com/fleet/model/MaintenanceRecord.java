/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "maintenance_records")
public class MaintenanceRecord {
    @Id
    @Column(name = "maintenance_id")
    private String maintenanceId;

    @Column(name = "vehicle_id")
    private String vehicleId;

    private LocalDate serviceDate;
    private String serviceType;
    private String description;
    private String serviceProvider;
    private BigDecimal cost;
    private LocalDate nextServiceDue;
    private Integer odometerReading;
}
