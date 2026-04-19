package com.fleet.controller;

// GRASP Controller: request parsing and response formatting stay here; business logic stays in services.
import com.fleet.dto.ApiResponse;
import com.fleet.dto.request.CreateDriverRequest;
import com.fleet.dto.request.UpdateDriverRequest;
import com.fleet.dto.response.DriverResponse;
import com.fleet.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/drivers")
public class DriverController {
    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<ApiResponse<DriverResponse>> createDriver(@RequestBody CreateDriverRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<DriverResponse>builder().success(true).data(driverService.createDriver(request)).build());
    }

    @GetMapping("/{id}")
        /**
         * Creates a new driver record.
         */
    public ResponseEntity<ApiResponse<DriverResponse>> getDriverById(@PathVariable("id") String driverId) {
        return ResponseEntity.ok(ApiResponse.<DriverResponse>builder().success(true).data(driverService.getDriverById(driverId)).build());
    }

    @PutMapping("/{id}")
        /**
         * Returns a driver by id.
         */
    public ResponseEntity<ApiResponse<DriverResponse>> updateDriver(@PathVariable("id") String driverId, @RequestBody UpdateDriverRequest request) {
        return ResponseEntity.ok(ApiResponse.<DriverResponse>builder().success(true).data(driverService.updateDriver(driverId, request)).build());
    }

    @DeleteMapping("/{id}")
        /**
         * Updates the driver identified by the supplied id.
         */
    public ResponseEntity<ApiResponse<Void>> deleteDriver(@PathVariable("id") String driverId) {
        driverService.deleteDriver(driverId);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).build());
    }

        /**
         * Deletes the driver identified by the supplied id.
         */
    @GetMapping("/{id}/assignments")
    public ResponseEntity<ApiResponse<Object>> getDriverAssignments(@PathVariable("id") String driverId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.<Object>builder().success(true).data(driverService.getDriverAssignments(driverId, pageable)).build());
    }

    @GetMapping("/{id}/trips")
        /**
         * Returns paged assignments for the specified driver.
         */
    public ResponseEntity<ApiResponse<Object>> getDriverTrips(@PathVariable("id") String driverId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.<Object>builder().success(true).data(driverService.getDriverTrips(driverId, pageable)).build());
    }
}

        /**
         * Returns paged trips for the specified driver.
         */
