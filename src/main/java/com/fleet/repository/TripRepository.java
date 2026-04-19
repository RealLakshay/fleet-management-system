/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.repository;

import com.fleet.model.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, String>, JpaSpecificationExecutor<Trip> {
    Page<Trip> findByVehicleId(String vehicleId, Pageable pageable);

    Page<Trip> findByDriverId(String driverId, Pageable pageable);

    List<Trip> findByVehicleId(String vehicleId, Sort sort);
}
