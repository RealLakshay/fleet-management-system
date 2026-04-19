/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.mapper;

// Pure Fabrication: mapping logic stays out of services and entities to keep the model cleaner.
import com.fleet.dto.request.CreateDriverRequest;
import com.fleet.dto.request.UpdateDriverRequest;
import com.fleet.dto.response.DriverResponse;
import com.fleet.model.Driver;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DriverMapper {
    public Driver toEntity(CreateDriverRequest request) {
        Driver driver = new Driver();
        driver.setDriverId(UUID.randomUUID().toString());
        updateFromRequest(request, driver);
        return driver;
    }

    public DriverResponse toResponse(Driver driver) {
        DriverResponse response = new DriverResponse();
        response.setDriverId(driver.getDriverId());
        response.setFirstName(driver.getFirstName());
        response.setLastName(driver.getLastName());
        response.setLicenseNumber(driver.getLicenseNumber());
        response.setLicenseType(driver.getLicenseType());
        response.setLicenseIssueDate(driver.getLicenseIssueDate());
        response.setLicenseExpiryDate(driver.getLicenseExpiryDate());
        response.setContactNumber(driver.getContactNumber());
        response.setEmail(driver.getEmail());
        response.setAddress(driver.getAddress());
        response.setAvailabilityStatus(driver.getAvailabilityStatus());
        return response;
    }

    public void updateFromRequest(CreateDriverRequest request, Driver driver) {
        if (request.getFirstName() != null) driver.setFirstName(request.getFirstName());
        if (request.getLastName() != null) driver.setLastName(request.getLastName());
        if (request.getLicenseNumber() != null) driver.setLicenseNumber(request.getLicenseNumber());
        if (request.getLicenseType() != null) driver.setLicenseType(request.getLicenseType());
        if (request.getLicenseIssueDate() != null) driver.setLicenseIssueDate(request.getLicenseIssueDate());
        if (request.getLicenseExpiryDate() != null) driver.setLicenseExpiryDate(request.getLicenseExpiryDate());
        if (request.getContactNumber() != null) driver.setContactNumber(request.getContactNumber());
        if (request.getEmail() != null) driver.setEmail(request.getEmail());
        if (request.getAddress() != null) driver.setAddress(request.getAddress());
        if (request.getAvailabilityStatus() != null) driver.setAvailabilityStatus(request.getAvailabilityStatus());
    }

    public void updateFromRequest(UpdateDriverRequest request, Driver driver) {
        if (request.getFirstName() != null) driver.setFirstName(request.getFirstName());
        if (request.getLastName() != null) driver.setLastName(request.getLastName());
        if (request.getLicenseNumber() != null) driver.setLicenseNumber(request.getLicenseNumber());
        if (request.getLicenseType() != null) driver.setLicenseType(request.getLicenseType());
        if (request.getLicenseIssueDate() != null) driver.setLicenseIssueDate(request.getLicenseIssueDate());
        if (request.getLicenseExpiryDate() != null) driver.setLicenseExpiryDate(request.getLicenseExpiryDate());
        if (request.getContactNumber() != null) driver.setContactNumber(request.getContactNumber());
        if (request.getEmail() != null) driver.setEmail(request.getEmail());
        if (request.getAddress() != null) driver.setAddress(request.getAddress());
        if (request.getAvailabilityStatus() != null) driver.setAvailabilityStatus(request.getAvailabilityStatus());
    }
}
