package com.fleet.service.impl;

import com.fleet.dto.request.CreateAssignmentRequest;
import com.fleet.dto.response.AssignmentResponse;
import com.fleet.exception.BusinessRuleException;
import com.fleet.exception.ResourceNotFoundException;
import com.fleet.mapper.AssignmentMapper;
import com.fleet.model.Assignment;
import com.fleet.model.Driver;
import com.fleet.model.Vehicle;
import com.fleet.model.enums.AssignmentStatus;
import com.fleet.model.enums.AvailabilityStatus;
import com.fleet.model.enums.VehicleStatus;
import com.fleet.repository.AssignmentRepository;
import com.fleet.repository.DriverRepository;
import com.fleet.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceImplTest {

    @Mock
    private AssignmentRepository assignmentRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private AssignmentMapper assignmentMapper;

    @InjectMocks
    private AssignmentServiceImpl assignmentService;

    @Test
    void createAssignment_happyPath() {
        CreateAssignmentRequest request = new CreateAssignmentRequest();
        request.setVehicleId("v1");
        request.setDriverId("d1");
        request.setAssignmentDate(LocalDate.now());

        Vehicle vehicle = new Vehicle();
        vehicle.setStatus(VehicleStatus.OPERATIONAL);

        Driver driver = new Driver();
        driver.setLicenseExpiryDate(LocalDate.now().plusDays(30));

        Assignment entity = new Assignment();
        Assignment saved = new Assignment();
        saved.setAssignmentId("a1");
        AssignmentResponse expected = new AssignmentResponse();
        expected.setAssignmentId("a1");

        when(vehicleRepository.findById("v1")).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById("d1")).thenReturn(Optional.of(driver));
        when(assignmentRepository.existsByDriverIdAndStatus("d1", AssignmentStatus.ACTIVE)).thenReturn(false);
        when(assignmentMapper.toEntity(request)).thenReturn(entity);
        when(assignmentRepository.save(entity)).thenReturn(saved);
        when(assignmentMapper.toResponse(saved)).thenReturn(expected);
        when(driverRepository.save(driver)).thenReturn(driver);

        AssignmentResponse actual = assignmentService.createAssignment(request);

        assertEquals("a1", actual.getAssignmentId());
        ArgumentCaptor<Assignment> captor = ArgumentCaptor.forClass(Assignment.class);
        verify(assignmentRepository).save(captor.capture());
        assertEquals(AssignmentStatus.ACTIVE, captor.getValue().getStatus());
        assertEquals(AvailabilityStatus.ON_TRIP, driver.getAvailabilityStatus());
    }

    @Test
    void createAssignment_whenVehicleNotOperational_throwsBusinessRuleException() {
        CreateAssignmentRequest request = new CreateAssignmentRequest();
        request.setVehicleId("v1");
        request.setDriverId("d1");

        Vehicle vehicle = new Vehicle();
        vehicle.setStatus(VehicleStatus.MAINTENANCE);
        Driver driver = new Driver();

        when(vehicleRepository.findById("v1")).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById("d1")).thenReturn(Optional.of(driver));

        assertThrows(BusinessRuleException.class, () -> assignmentService.createAssignment(request));
    }

    @Test
    void getAssignmentById_happyPath() {
        Assignment assignment = new Assignment();
        assignment.setAssignmentId("a1");
        AssignmentResponse expected = new AssignmentResponse();
        expected.setAssignmentId("a1");

        when(assignmentRepository.findById("a1")).thenReturn(Optional.of(assignment));
        when(assignmentMapper.toResponse(assignment)).thenReturn(expected);

        AssignmentResponse actual = assignmentService.getAssignmentById("a1");
        assertEquals("a1", actual.getAssignmentId());
    }

    @Test
    void getAssignmentById_unknownId_throwsResourceNotFound() {
        when(assignmentRepository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> assignmentService.getAssignmentById("missing"));
    }

    @Test
    void updateAssignment_happyPath() {
        Assignment existing = new Assignment();
        existing.setAssignmentId("a1");
        existing.setStatus(AssignmentStatus.ACTIVE);

        CreateAssignmentRequest request = new CreateAssignmentRequest();
        request.setNotes("Updated note");

        AssignmentResponse expected = new AssignmentResponse();
        expected.setAssignmentId("a1");

        when(assignmentRepository.findById("a1")).thenReturn(Optional.of(existing));
        when(assignmentRepository.save(existing)).thenReturn(existing);
        when(assignmentMapper.toResponse(existing)).thenReturn(expected);

        AssignmentResponse actual = assignmentService.updateAssignment("a1", request);

        assertEquals("Updated note", existing.getNotes());
        assertEquals("a1", actual.getAssignmentId());
    }

    @Test
    void deleteAssignment_happyPath() {
        Assignment existing = new Assignment();
        existing.setAssignmentId("a1");
        existing.setStatus(AssignmentStatus.COMPLETED);

        when(assignmentRepository.findById("a1")).thenReturn(Optional.of(existing));

        assignmentService.deleteAssignment("a1");

        verify(assignmentRepository).delete(existing);
    }
}
