package com.fleet.service;

import com.fleet.dto.request.CreateDriverRequest;
import com.fleet.dto.request.UpdateDriverRequest;
import com.fleet.dto.response.DriverResponse;
import org.springframework.data.domain.Pageable;

public interface DriverService {
    DriverResponse createDriver(CreateDriverRequest request);

    DriverResponse getDriverById(String driverId);

    DriverResponse updateDriver(String driverId, UpdateDriverRequest request);

    void deleteDriver(String driverId);

    Object getDriverAssignments(String driverId, Pageable pageable);

    Object getDriverTrips(String driverId, Pageable pageable);
}