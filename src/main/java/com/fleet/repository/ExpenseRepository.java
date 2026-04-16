package com.fleet.repository;

import com.fleet.model.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, String> {
    Page<Expense> findByVehicleId(String vehicleId, Pageable pageable);
}