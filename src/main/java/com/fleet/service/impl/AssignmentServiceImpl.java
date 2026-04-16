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
import com.fleet.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentServiceImpl implements AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final AssignmentMapper assignmentMapper;

    @Override
    @Transactional
    public AssignmentResponse createAssignment(CreateAssignmentRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "vehicleId", request.getVehicleId()));
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "driverId", request.getDriverId()));

        if (vehicle.getStatus() != VehicleStatus.OPERATIONAL) {
            throw new BusinessRuleException("Vehicle must be operational");
        }
        if (assignmentRepository.existsByDriverIdAndStatus(request.getDriverId(), AssignmentStatus.ACTIVE)) {
            throw new BusinessRuleException("Driver already has an active assignment");
        }

        Assignment assignment = assignmentMapper.toEntity(request);
        if (assignment.getStatus() == null) {
            assignment.setStatus(AssignmentStatus.ACTIVE);
        }
        Assignment saved = assignmentRepository.save(assignment);

        driver.setAvailabilityStatus(AvailabilityStatus.ON_TRIP);
        driverRepository.save(driver);

        return assignmentMapper.toResponse(saved);
    }

    @Override
    public AssignmentResponse getAssignmentById(String assignmentId) {
        return assignmentMapper.toResponse(findAssignmentById(assignmentId));
    }

    @Override
    @Transactional
    public AssignmentResponse updateAssignment(String assignmentId, CreateAssignmentRequest request) {
        Assignment existing = findAssignmentById(assignmentId);
        if (request.getVehicleId() != null) {
            existing.setVehicleId(request.getVehicleId());
        }
        if (request.getDriverId() != null) {
            existing.setDriverId(request.getDriverId());
        }
        if (request.getAssignmentDate() != null) {
            existing.setAssignmentDate(request.getAssignmentDate());
        }
        if (request.getStartDate() != null) {
            existing.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            existing.setEndDate(request.getEndDate());
        }
        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }
        if (request.getNotes() != null) {
            existing.setNotes(request.getNotes());
        }
        return assignmentMapper.toResponse(assignmentRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteAssignment(String assignmentId) {
        Assignment existing = findAssignmentById(assignmentId);
        assignmentRepository.delete(existing);
    }

    private Assignment findAssignmentById(String assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "assignmentId", assignmentId));
    }
}