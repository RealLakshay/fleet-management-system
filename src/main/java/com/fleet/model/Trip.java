package com.fleet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "trips")
public class Trip {
    @Id
    @Column(name = "trip_id")
    private String tripId;

    @Column(name = "vehicle_id")
    private String vehicleId;

    @Column(name = "driver_id")
    private String driverId;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String purpose;
    private String startLocation;
    private String endLocation;
    private Long startOdometer;
    private Long endOdometer;
    private BigDecimal distanceCovered;
}