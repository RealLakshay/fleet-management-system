package com.fleet.controller;

// GRASP Controller: HTTP routing and validation happen here; fleet rules live in the service layer.
import com.fleet.dto.ApiResponse;
import com.fleet.dto.PagedResponse;
import com.fleet.dto.request.CreateVehicleRequest;
import com.fleet.dto.request.UpdateVehicleRequest;
import com.fleet.dto.response.AssignmentResponse;
import com.fleet.dto.response.ExpenseResponse;
import com.fleet.dto.response.MaintenanceResponse;
import com.fleet.dto.response.TripResponse;
import com.fleet.dto.response.VehicleResponse;
import com.fleet.model.enums.VehicleStatus;
import com.fleet.service.VehicleService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    /**
     * Creates a new vehicle and returns the saved resource.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<VehicleResponse>> createVehicle(@RequestBody CreateVehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<VehicleResponse>builder().success(true).data(vehicleService.createVehicle(request)).build());
    }

    /**
     * Returns a filtered, paged list of vehicles.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<VehicleResponse>>> getVehicles(
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) String vehicleType,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.<PagedResponse<VehicleResponse>>builder().success(true).data(vehicleService.getVehicles(status, vehicleType, search, pageable)).build());
    }

    /**
     * Returns a vehicle by its identifier.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> getVehicleById(@PathVariable("id") String vehicleId) {
        return ResponseEntity.ok(ApiResponse.<VehicleResponse>builder().success(true).data(vehicleService.getVehicleById(vehicleId)).build());
    }

    /**
     * Updates the vehicle identified by the supplied id.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> updateVehicle(@PathVariable("id") String vehicleId, @RequestBody UpdateVehicleRequest request) {
        return ResponseEntity.ok(ApiResponse.<VehicleResponse>builder().success(true).data(vehicleService.updateVehicle(vehicleId, request)).build());
    }

    /**
     * Deletes the vehicle identified by the supplied id.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(@PathVariable("id") String vehicleId) {
        vehicleService.deleteVehicle(vehicleId);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).build());
    }

    /**
     * Returns paged assignments for the selected vehicle.
     */
    @GetMapping("/{id}/assignments")
    public ResponseEntity<ApiResponse<PagedResponse<AssignmentResponse>>> getVehicleAssignments(@PathVariable("id") String vehicleId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.<PagedResponse<AssignmentResponse>>builder().success(true).data(vehicleService.getVehicleAssignments(vehicleId, pageable)).build());
    }

    /**
     * Returns paged maintenance records for the selected vehicle.
     */
    @GetMapping("/{id}/maintenance")
    public ResponseEntity<ApiResponse<PagedResponse<MaintenanceResponse>>> getVehicleMaintenance(@PathVariable("id") String vehicleId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.<PagedResponse<MaintenanceResponse>>builder().success(true).data(vehicleService.getVehicleMaintenance(vehicleId, pageable)).build());
    }

    /**
     * Returns paged expenses for the selected vehicle.
     */
    @GetMapping("/{id}/expenses")
    public ResponseEntity<ApiResponse<PagedResponse<ExpenseResponse>>> getVehicleExpenses(@PathVariable("id") String vehicleId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.<PagedResponse<ExpenseResponse>>builder().success(true).data(vehicleService.getVehicleExpenses(vehicleId, pageable)).build());
    }

    /**
     * Returns paged trips for the selected vehicle.
     */
    @GetMapping("/{id}/trips")
    public ResponseEntity<ApiResponse<PagedResponse<TripResponse>>> getVehicleTrips(@PathVariable("id") String vehicleId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.<PagedResponse<TripResponse>>builder().success(true).data(vehicleService.getVehicleTrips(vehicleId, pageable)).build());
    }
}
