package com.fleet.service;

import com.fleet.dto.request.CreateAssignmentRequest;
import com.fleet.dto.response.AssignmentResponse;

public interface AssignmentService {
    /**
     * Creates a new assignment after validating the target vehicle and driver.
     */
    AssignmentResponse createAssignment(CreateAssignmentRequest request);

    /**
     * Returns one assignment by id.
     */
    AssignmentResponse getAssignmentById(String assignmentId);

    /**
     * Updates an existing assignment.
     */
    AssignmentResponse updateAssignment(String assignmentId, CreateAssignmentRequest request);

    /**
     * Deletes an assignment by id.
     */
    void deleteAssignment(String assignmentId);
}
