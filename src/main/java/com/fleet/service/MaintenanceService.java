package com.fleet.service;

import com.fleet.dto.request.CreateMaintenanceRequest;
import com.fleet.dto.response.MaintenanceResponse;

public interface MaintenanceService {
    /**
     * Creates a maintenance record and marks the vehicle as in maintenance.
     */
    MaintenanceResponse createMaintenance(CreateMaintenanceRequest request);

    /**
     * Returns one maintenance record by id.
     */
    MaintenanceResponse getMaintenanceById(String maintenanceId);

    /**
     * Updates an existing maintenance record.
     */
    MaintenanceResponse updateMaintenance(String maintenanceId, CreateMaintenanceRequest request);

    /**
     * Deletes a maintenance record by id.
     */
    void deleteMaintenance(String maintenanceId);
}
