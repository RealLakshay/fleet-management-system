/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.dto.request;

import com.fleet.model.enums.VehicleStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateVehicleRequest {
    private String registrationNumber;
    private String vehicleType;
    private String manufacturer;
    private String model;
    private Integer year;
    private VehicleStatus status;
    private LocalDate purchaseDate;
    private String ownershipDetails;
    private Integer currentOdometer;
}




