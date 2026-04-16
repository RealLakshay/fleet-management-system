package com.fleet.service;

import com.fleet.dto.request.CreateAssignmentRequest;
import com.fleet.dto.response.AssignmentResponse;

public interface AssignmentService {
    AssignmentResponse createAssignment(CreateAssignmentRequest request);

    AssignmentResponse getAssignmentById(String assignmentId);

    AssignmentResponse updateAssignment(String assignmentId, CreateAssignmentRequest request);

    void deleteAssignment(String assignmentId);
}