package com.fleet.dto.request;

import com.fleet.model.enums.AvailabilityStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateDriverRequest {
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