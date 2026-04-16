package com.fleet.mapper;

// Pure Fabrication: maintenance request/response conversion is centralized here.
import com.fleet.dto.request.CreateMaintenanceRequest;
import com.fleet.dto.response.MaintenanceResponse;
import com.fleet.model.MaintenanceRecord;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MaintenanceMapper {
    public MaintenanceRecord toEntity(CreateMaintenanceRequest request) {
        MaintenanceRecord record = new MaintenanceRecord();
        record.setMaintenanceId(UUID.randomUUID().toString());
        updateFromRequest(request, record);
        return record;
    }

    public MaintenanceResponse toResponse(MaintenanceRecord record) {
        MaintenanceResponse response = new MaintenanceResponse();
        response.setMaintenanceId(record.getMaintenanceId());
        response.setVehicleId(record.getVehicleId());
        response.setServiceDate(record.getServiceDate());
        response.setServiceType(record.getServiceType());
        response.setDescription(record.getDescription());
        response.setServiceProvider(record.getServiceProvider());
        response.setCost(record.getCost());
        response.setNextServiceDue(record.getNextServiceDue());
        response.setOdometerReading(record.getOdometerReading());
        return response;
    }

    public void updateFromRequest(CreateMaintenanceRequest request, MaintenanceRecord record) {
        if (request.getVehicleId() != null) record.setVehicleId(request.getVehicleId());
        if (request.getServiceDate() != null) record.setServiceDate(request.getServiceDate());
        if (request.getServiceType() != null) record.setServiceType(request.getServiceType());
        if (request.getDescription() != null) record.setDescription(request.getDescription());
        if (request.getServiceProvider() != null) record.setServiceProvider(request.getServiceProvider());
        if (request.getCost() != null) record.setCost(request.getCost());
        if (request.getNextServiceDue() != null) record.setNextServiceDue(request.getNextServiceDue());
        if (request.getOdometerReading() != null) record.setOdometerReading(request.getOdometerReading());
    }
}