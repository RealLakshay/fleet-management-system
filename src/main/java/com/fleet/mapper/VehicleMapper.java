package com.fleet.mapper;

// Pure Fabrication: vehicle DTO conversion is kept separate from the domain entity and service flow.
import com.fleet.dto.request.CreateVehicleRequest;
import com.fleet.dto.request.UpdateVehicleRequest;
import com.fleet.dto.response.VehicleResponse;
import com.fleet.model.Vehicle;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VehicleMapper {
    public Vehicle toEntity(CreateVehicleRequest request) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(UUID.randomUUID().toString());
        updateFromRequest(request, vehicle);
        return vehicle;
    }

    public VehicleResponse toResponse(Vehicle vehicle) {
        VehicleResponse response = new VehicleResponse();
        response.setVehicleId(vehicle.getVehicleId());
        response.setRegistrationNumber(vehicle.getRegistrationNumber());
        response.setVehicleType(vehicle.getVehicleType());
        response.setManufacturer(vehicle.getManufacturer());
        response.setModel(vehicle.getModel());
        response.setYear(vehicle.getYear());
        response.setStatus(vehicle.getStatus());
        response.setPurchaseDate(vehicle.getPurchaseDate());
        response.setOwnershipDetails(vehicle.getOwnershipDetails());
        response.setCurrentOdometer(vehicle.getCurrentOdometer());
        return response;
    }

    public void updateFromRequest(UpdateVehicleRequest request, Vehicle vehicle) {
        if (request.getRegistrationNumber() != null) vehicle.setRegistrationNumber(request.getRegistrationNumber());
        if (request.getVehicleType() != null) vehicle.setVehicleType(request.getVehicleType());
        if (request.getManufacturer() != null) vehicle.setManufacturer(request.getManufacturer());
        if (request.getModel() != null) vehicle.setModel(request.getModel());
        if (request.getYear() != null) vehicle.setYear(request.getYear());
        if (request.getStatus() != null) vehicle.setStatus(request.getStatus());
        if (request.getPurchaseDate() != null) vehicle.setPurchaseDate(request.getPurchaseDate());
        if (request.getOwnershipDetails() != null) vehicle.setOwnershipDetails(request.getOwnershipDetails());
        if (request.getCurrentOdometer() != null) vehicle.setCurrentOdometer(request.getCurrentOdometer());
    }

    public void updateFromRequest(CreateVehicleRequest request, Vehicle vehicle) {
        if (request.getRegistrationNumber() != null) vehicle.setRegistrationNumber(request.getRegistrationNumber());
        if (request.getVehicleType() != null) vehicle.setVehicleType(request.getVehicleType());
        if (request.getManufacturer() != null) vehicle.setManufacturer(request.getManufacturer());
        if (request.getModel() != null) vehicle.setModel(request.getModel());
        if (request.getYear() != null) vehicle.setYear(request.getYear());
        if (request.getStatus() != null) vehicle.setStatus(request.getStatus());
        if (request.getPurchaseDate() != null) vehicle.setPurchaseDate(request.getPurchaseDate());
        if (request.getOwnershipDetails() != null) vehicle.setOwnershipDetails(request.getOwnershipDetails());
        if (request.getCurrentOdometer() != null) vehicle.setCurrentOdometer(request.getCurrentOdometer());
    }
}