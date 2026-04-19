/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.model;

import com.fleet.model.enums.AvailabilityStatus;
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
@Table(name = "drivers")
public class Driver {
    @Id
    @Column(name = "driver_id")
    private String driverId;

    private String firstName;
    private String lastName;

    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    private String licenseType;
    private LocalDate licenseIssueDate;
    private LocalDate licenseExpiryDate;
    private String contactNumber;

    @Column(nullable = false, unique = true)
    private String email;

    private String address;
    @Enumerated(EnumType.STRING)
    private AvailabilityStatus availabilityStatus;
}
