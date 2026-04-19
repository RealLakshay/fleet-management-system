/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.dto.request;

import com.fleet.model.enums.AssignmentStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateAssignmentRequest {
    private String vehicleId;
    private String driverId;
    private LocalDate assignmentDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private AssignmentStatus status;
    private String notes;
}
