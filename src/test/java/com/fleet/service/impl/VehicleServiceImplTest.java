/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.service.impl;

import com.fleet.dto.request.CreateVehicleRequest;
import com.fleet.dto.request.UpdateVehicleRequest;
import com.fleet.dto.response.VehicleResponse;
import com.fleet.exception.BusinessRuleException;
import com.fleet.exception.ResourceNotFoundException;
import com.fleet.mapper.AssignmentMapper;
import com.fleet.mapper.ExpenseMapper;
import com.fleet.mapper.MaintenanceMapper;
import com.fleet.mapper.TripMapper;
import com.fleet.mapper.VehicleMapper;
import com.fleet.model.Vehicle;
import com.fleet.model.enums.AssignmentStatus;
import com.fleet.repository.AssignmentRepository;
import com.fleet.repository.ExpenseRepository;
import com.fleet.repository.MaintenanceRepository;
import com.fleet.repository.TripRepository;
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
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private AssignmentRepository assignmentRepository;
    @Mock
    private MaintenanceRepository maintenanceRepository;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private TripRepository tripRepository;
    @Mock
    private VehicleMapper vehicleMapper;
    @Mock
    private AssignmentMapper assignmentMapper;
    @Mock
    private MaintenanceMapper maintenanceMapper;
    @Mock
    private ExpenseMapper expenseMapper;
    @Mock
    private TripMapper tripMapper;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    @Test
    void createVehicle_happyPath() {
        CreateVehicleRequest request = new CreateVehicleRequest();
        request.setRegistrationNumber("KA-01-1234");

        Vehicle entity = new Vehicle();
        Vehicle saved = new Vehicle();
        saved.setVehicleId("v1");
        VehicleResponse expected = new VehicleResponse();
        expected.setVehicleId("v1");

        when(vehicleRepository.existsByRegistrationNumber("KA-01-1234")).thenReturn(false);
        when(vehicleMapper.toEntity(request)).thenReturn(entity);
        when(vehicleRepository.save(entity)).thenReturn(saved);
        when(vehicleMapper.toResponse(saved)).thenReturn(expected);

        VehicleResponse actual = vehicleService.createVehicle(request);

        assertEquals("v1", actual.getVehicleId());
    }

    @Test
    void getVehicleById_happyPath() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId("v1");
        VehicleResponse expected = new VehicleResponse();
        expected.setVehicleId("v1");

        when(vehicleRepository.findById("v1")).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toResponse(vehicle)).thenReturn(expected);

        VehicleResponse actual = vehicleService.getVehicleById("v1");

        assertEquals("v1", actual.getVehicleId());
    }

    @Test
    void getVehicleById_unknownId_throwsResourceNotFound() {
        when(vehicleRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehicleService.getVehicleById("missing"));
    }

    @Test
    void updateVehicle_happyPath() {
        UpdateVehicleRequest request = new UpdateVehicleRequest();
        request.setModel("UpdatedModel");

        Vehicle existing = new Vehicle();
        existing.setVehicleId("v1");
        existing.setRegistrationNumber("KA-01-1234");
        VehicleResponse expected = new VehicleResponse();
        expected.setVehicleId("v1");

        when(vehicleRepository.findById("v1")).thenReturn(Optional.of(existing));
        when(vehicleRepository.save(existing)).thenReturn(existing);
        when(vehicleMapper.toResponse(existing)).thenReturn(expected);

        VehicleResponse actual = vehicleService.updateVehicle("v1", request);

        verify(vehicleMapper).updateFromRequest(request, existing);
        assertEquals("v1", actual.getVehicleId());
    }

    @Test
    void deleteVehicle_happyPath() {
        Vehicle existing = new Vehicle();
        existing.setVehicleId("v1");

        when(vehicleRepository.findById("v1")).thenReturn(Optional.of(existing));
        when(assignmentRepository.existsByVehicleIdAndStatus("v1", AssignmentStatus.ACTIVE)).thenReturn(false);

        vehicleService.deleteVehicle("v1");

        verify(vehicleRepository).delete(existing);
    }

    @Test
    void deleteVehicle_withActiveAssignment_throwsBusinessRuleException() {
        Vehicle existing = new Vehicle();
        existing.setVehicleId("v1");

        when(vehicleRepository.findById("v1")).thenReturn(Optional.of(existing));
        when(assignmentRepository.existsByVehicleIdAndStatus("v1", AssignmentStatus.ACTIVE)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> vehicleService.deleteVehicle("v1"));
    }
}

