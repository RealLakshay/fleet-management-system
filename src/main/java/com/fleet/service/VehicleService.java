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
    VehicleResponse createVehicle(CreateVehicleRequest request);

    PagedResponse<VehicleResponse> getVehicles(VehicleStatus status, String vehicleType, String search, Pageable pageable);

    VehicleResponse getVehicleById(String vehicleId);

    VehicleResponse updateVehicle(String vehicleId, UpdateVehicleRequest request);

    void deleteVehicle(String vehicleId);

    PagedResponse<AssignmentResponse> getVehicleAssignments(String vehicleId, Pageable pageable);

    PagedResponse<MaintenanceResponse> getVehicleMaintenance(String vehicleId, Pageable pageable);

    PagedResponse<ExpenseResponse> getVehicleExpenses(String vehicleId, Pageable pageable);

    PagedResponse<TripResponse> getVehicleTrips(String vehicleId, Pageable pageable);
}