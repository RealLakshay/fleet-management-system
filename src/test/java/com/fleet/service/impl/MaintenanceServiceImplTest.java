/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.service.impl;

import com.fleet.dto.request.CreateMaintenanceRequest;
import com.fleet.dto.response.MaintenanceResponse;
import com.fleet.exception.ResourceNotFoundException;
import com.fleet.mapper.MaintenanceMapper;
import com.fleet.model.MaintenanceRecord;
import com.fleet.model.Vehicle;
import com.fleet.model.enums.VehicleStatus;
import com.fleet.repository.MaintenanceRepository;
import com.fleet.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaintenanceServiceImplTest {

    @Mock
    private MaintenanceRepository maintenanceRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private MaintenanceMapper maintenanceMapper;

    @InjectMocks
    private MaintenanceServiceImpl maintenanceService;

    @Test
    void createMaintenance_happyPath() {
        CreateMaintenanceRequest request = new CreateMaintenanceRequest();
        request.setVehicleId("v1");

        Vehicle vehicle = new Vehicle();
        MaintenanceRecord entity = new MaintenanceRecord();
        MaintenanceRecord saved = new MaintenanceRecord();
        saved.setMaintenanceId("m1");
        MaintenanceResponse expected = new MaintenanceResponse();
        expected.setMaintenanceId("m1");

        when(vehicleRepository.findById("v1")).thenReturn(Optional.of(vehicle));
        when(maintenanceMapper.toEntity(request)).thenReturn(entity);
        when(maintenanceRepository.save(entity)).thenReturn(saved);
        when(maintenanceMapper.toResponse(saved)).thenReturn(expected);
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);

        MaintenanceResponse actual = maintenanceService.createMaintenance(request);

        assertEquals("m1", actual.getMaintenanceId());
        assertEquals(VehicleStatus.MAINTENANCE, vehicle.getStatus());
    }

    @Test
    void getMaintenanceById_happyPath() {
        MaintenanceRecord record = new MaintenanceRecord();
        record.setMaintenanceId("m1");
        MaintenanceResponse expected = new MaintenanceResponse();
        expected.setMaintenanceId("m1");

        when(maintenanceRepository.findById("m1")).thenReturn(Optional.of(record));
        when(maintenanceMapper.toResponse(record)).thenReturn(expected);

        MaintenanceResponse actual = maintenanceService.getMaintenanceById("m1");
        assertEquals("m1", actual.getMaintenanceId());
    }

    @Test
    void getMaintenanceById_unknownId_throwsResourceNotFound() {
        when(maintenanceRepository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> maintenanceService.getMaintenanceById("missing"));
    }

    @Test
    void updateMaintenance_happyPath() {
        MaintenanceRecord existing = new MaintenanceRecord();
        existing.setMaintenanceId("m1");

        CreateMaintenanceRequest request = new CreateMaintenanceRequest();
        request.setDescription("Updated description");

        MaintenanceResponse expected = new MaintenanceResponse();
        expected.setMaintenanceId("m1");

        when(maintenanceRepository.findById("m1")).thenReturn(Optional.of(existing));
        when(maintenanceRepository.save(existing)).thenReturn(existing);
        when(maintenanceMapper.toResponse(existing)).thenReturn(expected);

        MaintenanceResponse actual = maintenanceService.updateMaintenance("m1", request);

        assertEquals("Updated description", existing.getDescription());
        assertEquals("m1", actual.getMaintenanceId());
    }

    @Test
    void deleteMaintenance_happyPath() {
        MaintenanceRecord existing = new MaintenanceRecord();
        existing.setMaintenanceId("m1");

        when(maintenanceRepository.findById("m1")).thenReturn(Optional.of(existing));

        maintenanceService.deleteMaintenance("m1");

        verify(maintenanceRepository).delete(existing);
    }
}

