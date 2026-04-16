package com.fleet.dto.response;

import com.fleet.model.enums.AvailabilityStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DriverResponse {
    private String driverId;
    private String firstName;
    private String lastName;
    private String licenseNumber;
    private String licenseType;
    private LocalDate licenseIssueDate;
    private LocalDate licenseExpiryDate;
    private String contactNumber;
    private String email;
    private String address;
    private AvailabilityStatus availabilityStatus;
}