package com.fleet.service;

import com.fleet.dto.PagedResponse;
import com.fleet.dto.request.CreateVehicleRequest;
import com.fleet.dto.request.UpdateVehicleRequest;
import com.fleet.dto.response.AssignmentResponse;
import com.fleet.dto.response.ExpenseResponse;
import com.fleet.dto.response.MaintenanceResponse;
import com.fleet.dto.response.TripResponse;
import com.fleet.dto.response.VehicleResponse;
import com.fleet.model.enums.VehicleStatus;
import org.springframework.data.domain.Pageable;

public interface VehicleService {
    /**
     * Creates a new vehicle.
     */
    VehicleResponse createVehicle(CreateVehicleRequest request);

    /**
     * Returns a filtered, paged list of vehicles.
     */
    PagedResponse<VehicleResponse> getVehicles(VehicleStatus status, String vehicleType, String search, Pageable pageable);

    /**
     * Returns one vehicle by id.
     */
    VehicleResponse getVehicleById(String vehicleId);

    /**
     * Updates an existing vehicle.
     */
    VehicleResponse updateVehicle(String vehicleId, UpdateVehicleRequest request);

    /**
     * Deletes a vehicle by id.
     */
    void deleteVehicle(String vehicleId);

    /**
     * Returns paged assignments for a vehicle.
     */
    PagedResponse<AssignmentResponse> getVehicleAssignments(String vehicleId, Pageable pageable);

    /**
     * Returns paged maintenance records for a vehicle.
     */
    PagedResponse<MaintenanceResponse> getVehicleMaintenance(String vehicleId, Pageable pageable);

    /**
     * Returns paged expenses for a vehicle.
     */
    PagedResponse<ExpenseResponse> getVehicleExpenses(String vehicleId, Pageable pageable);

    /**
     * Returns paged trips for a vehicle.
     */
    PagedResponse<TripResponse> getVehicleTrips(String vehicleId, Pageable pageable);
}
