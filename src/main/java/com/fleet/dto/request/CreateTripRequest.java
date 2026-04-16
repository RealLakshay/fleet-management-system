package com.fleet.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTripRequest {
    private String vehicleId;
    private String driverId;
    private LocalDateTime startDate;
    private String purpose;
    private String startLocation;
    private String endLocation;
    private Long startOdometer;
    private Long endOdometer;
}