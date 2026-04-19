/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.repository;

import com.fleet.model.Assignment;
import com.fleet.model.enums.AssignmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, String> {
    boolean existsByVehicleIdAndStatus(String vehicleId, AssignmentStatus status);

    boolean existsByDriverIdAndStatus(String driverId, AssignmentStatus status);

    boolean existsByVehicleIdAndDriverIdAndStatus(String vehicleId, String driverId, AssignmentStatus status);

    Page<Assignment> findByVehicleId(String vehicleId, Pageable pageable);

    Page<Assignment> findByDriverId(String driverId, Pageable pageable);
}
