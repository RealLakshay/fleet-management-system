/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.repository;

import com.fleet.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VehicleRepository extends JpaRepository<Vehicle, String>, JpaSpecificationExecutor<Vehicle> {
    boolean existsByRegistrationNumber(String registrationNumber);
}
