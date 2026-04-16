package com.fleet.model;

import com.fleet.model.enums.AssignmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "assignments")
public class Assignment {
    @Id
    @Column(name = "assignment_id")
    private String assignmentId;

    @Column(name = "vehicle_id")
    private String vehicleId;

    @Column(name = "driver_id")
    private String driverId;

    private LocalDate assignmentDate;
    private LocalDate startDate;
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private AssignmentStatus status;
    private String notes;
}