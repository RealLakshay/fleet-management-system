package com.fleet.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TripResponse {
    private String tripId;
    private String vehicleId;
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