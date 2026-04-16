package com.fleet.repository;

import com.fleet.model.MaintenanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceRepository extends JpaRepository<MaintenanceRecord, String> {
    Page<MaintenanceRecord> findByVehicleId(String vehicleId, Pageable pageable);
}