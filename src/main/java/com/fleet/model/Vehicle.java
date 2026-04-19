/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.model;

import com.fleet.model.enums.VehicleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @Column(name = "vehicle_id")
    private String vehicleId;

    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;

    @Column(name = "vehicle_type")
    private String vehicleType;

    private String manufacturer;
    private String model;
    @Column(name = "vehicle_year")
    private Integer year;
    @Enumerated(EnumType.STRING)
    private VehicleStatus status;
    private LocalDate purchaseDate;
    private String ownershipDetails;
    private Integer currentOdometer;
}
