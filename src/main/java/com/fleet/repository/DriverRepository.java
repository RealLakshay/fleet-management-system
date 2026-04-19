/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.repository;

import com.fleet.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, String> {
    boolean existsByLicenseNumber(String licenseNumber);

    boolean existsByEmail(String email);
}
