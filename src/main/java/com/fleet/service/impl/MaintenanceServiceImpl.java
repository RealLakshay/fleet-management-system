package com.fleet.service.impl;

// SRP + Information Expert: maintenance rules stay with the maintenance use case instead of leaking into controllers.
import com.fleet.dto.request.CreateMaintenanceRequest;
import com.fleet.dto.response.MaintenanceResponse;
import com.fleet.exception.ResourceNotFoundException;
import com.fleet.mapper.MaintenanceMapper;
import com.fleet.model.MaintenanceRecord;
import com.fleet.model.Vehicle;
import com.fleet.model.enums.VehicleStatus;
import com.fleet.repository.MaintenanceRepository;
import com.fleet.repository.VehicleRepository;
import com.fleet.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaintenanceServiceImpl implements MaintenanceService {
    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;
    private final MaintenanceMapper maintenanceMapper;

    /**
     * Creates a maintenance record and marks the linked vehicle as under maintenance.
     */
    @Override
    @Transactional
    public MaintenanceResponse createMaintenance(CreateMaintenanceRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "vehicleId", request.getVehicleId()));
        MaintenanceRecord record = maintenanceMapper.toEntity(request);
        vehicle.setStatus(VehicleStatus.MAINTENANCE);
        vehicleRepository.save(vehicle);
        return maintenanceMapper.toResponse(maintenanceRepository.save(record));
    }

    /**
     * Returns one maintenance record by id.
     */
    @Override
    public MaintenanceResponse getMaintenanceById(String maintenanceId) {
        return maintenanceMapper.toResponse(findMaintenanceById(maintenanceId));
    }

    /**
     * Updates only the maintenance fields provided in the request.
     */
    @Override
    @Transactional
    public MaintenanceResponse updateMaintenance(String maintenanceId, CreateMaintenanceRequest request) {
        MaintenanceRecord existing = findMaintenanceById(maintenanceId);
        if (request.getVehicleId() != null) {
            existing.setVehicleId(request.getVehicleId());
        }
        if (request.getServiceDate() != null) {
            existing.setServiceDate(request.getServiceDate());
        }
        if (request.getServiceType() != null) {
            existing.setServiceType(request.getServiceType());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getServiceProvider() != null) {
            existing.setServiceProvider(request.getServiceProvider());
        }
        if (request.getCost() != null) {
            existing.setCost(request.getCost());
        }
        if (request.getNextServiceDue() != null) {
            existing.setNextServiceDue(request.getNextServiceDue());
        }
        if (request.getOdometerReading() != null) {
            existing.setOdometerReading(request.getOdometerReading());
        }
        return maintenanceMapper.toResponse(maintenanceRepository.save(existing));
    }

    /**
     * Deletes the maintenance record by id.
     */
    @Override
    @Transactional
    public void deleteMaintenance(String maintenanceId) {
        maintenanceRepository.delete(findMaintenanceById(maintenanceId));
    }

    /**
     * Loads a maintenance record or throws a not found exception.
     */
    private MaintenanceRecord findMaintenanceById(String maintenanceId) {
        return maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", "maintenanceId", maintenanceId));
    }
}
