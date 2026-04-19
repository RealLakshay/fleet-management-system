package com.fleet.service;

import com.fleet.dto.request.CreateDriverRequest;
import com.fleet.dto.request.UpdateDriverRequest;
import com.fleet.dto.response.DriverResponse;
import org.springframework.data.domain.Pageable;

public interface DriverService {
    /**
     * Creates a new driver record.
     */
    DriverResponse createDriver(CreateDriverRequest request);

    /**
     * Returns one driver by id.
     */
    DriverResponse getDriverById(String driverId);

    /**
     * Updates an existing driver.
     */
    DriverResponse updateDriver(String driverId, UpdateDriverRequest request);

    /**
     * Deletes a driver by id.
     */
    void deleteDriver(String driverId);

    /**
     * Returns the driver's assignments as a paged response.
     */
    Object getDriverAssignments(String driverId, Pageable pageable);

    /**
     * Returns the driver's trips as a paged response.
     */
    Object getDriverTrips(String driverId, Pageable pageable);
}
