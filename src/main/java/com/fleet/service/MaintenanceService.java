package com.fleet.service;

import com.fleet.dto.request.CreateMaintenanceRequest;
import com.fleet.dto.response.MaintenanceResponse;

public interface MaintenanceService {
    MaintenanceResponse createMaintenance(CreateMaintenanceRequest request);

    MaintenanceResponse getMaintenanceById(String maintenanceId);

    MaintenanceResponse updateMaintenance(String maintenanceId, CreateMaintenanceRequest request);

    void deleteMaintenance(String maintenanceId);
}