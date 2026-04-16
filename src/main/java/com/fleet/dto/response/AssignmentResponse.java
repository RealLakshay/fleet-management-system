package com.fleet.dto.response;

import com.fleet.model.enums.AssignmentStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AssignmentResponse {
    private String assignmentId;
    private String vehicleId;
    private String driverId;
    private LocalDate assignmentDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private AssignmentStatus status;
    private String notes;
}