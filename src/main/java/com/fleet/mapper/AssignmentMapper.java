/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.mapper;

// Pure Fabrication: DTO mapping is isolated here so entities and services stay focused on domain rules.
import com.fleet.dto.request.CreateAssignmentRequest;
import com.fleet.dto.response.AssignmentResponse;
import com.fleet.model.Assignment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AssignmentMapper {
    public Assignment toEntity(CreateAssignmentRequest request) {
        Assignment assignment = new Assignment();
        assignment.setAssignmentId(UUID.randomUUID().toString());
        updateFromRequest(request, assignment);
        return assignment;
    }

    public AssignmentResponse toResponse(Assignment assignment) {
        AssignmentResponse response = new AssignmentResponse();
        response.setAssignmentId(assignment.getAssignmentId());
        response.setVehicleId(assignment.getVehicleId());
        response.setDriverId(assignment.getDriverId());
        response.setAssignmentDate(assignment.getAssignmentDate());
        response.setStartDate(assignment.getStartDate());
        response.setEndDate(assignment.getEndDate());
        response.setStatus(assignment.getStatus());
        response.setNotes(assignment.getNotes());
        return response;
    }

    public void updateFromRequest(CreateAssignmentRequest request, Assignment assignment) {
        if (request.getVehicleId() != null) assignment.setVehicleId(request.getVehicleId());
        if (request.getDriverId() != null) assignment.setDriverId(request.getDriverId());
        if (request.getAssignmentDate() != null) assignment.setAssignmentDate(request.getAssignmentDate());
        if (request.getStartDate() != null) assignment.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) assignment.setEndDate(request.getEndDate());
        if (request.getStatus() != null) assignment.setStatus(request.getStatus());
        if (request.getNotes() != null) assignment.setNotes(request.getNotes());
    }
}
