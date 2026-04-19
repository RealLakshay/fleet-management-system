/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.service.impl;

import com.fleet.dto.response.ReportResponse;
import com.fleet.exception.ResourceNotFoundException;
import com.fleet.model.Trip;
import com.fleet.repository.DriverRepository;
import com.fleet.repository.ExpenseRepository;
import com.fleet.repository.TripRepository;
import com.fleet.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private TripRepository tripRepository;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    void getVehicleUsage_happyPath() {
        Trip trip1 = new Trip();
        trip1.setStartDate(LocalDateTime.of(2026, 1, 10, 10, 0));
        trip1.setDistanceCovered(new BigDecimal("15"));
        Trip trip2 = new Trip();
        trip2.setStartDate(LocalDateTime.of(2026, 1, 10, 12, 0));
        trip2.setDistanceCovered(new BigDecimal("10"));

        when(vehicleRepository.existsById("v1")).thenReturn(true);
        when(tripRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(trip1, trip2));

        List<ReportResponse> result = reportService.getVehicleUsage("v1", null, null, "DAY");

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getTripCount());
        assertEquals(new BigDecimal("25"), result.get(0).getTotalDistance());
    }

    @Test
    void getVehicleUsage_unknownVehicle_throwsResourceNotFound() {
        when(vehicleRepository.existsById("missing")).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> reportService.getVehicleUsage("missing", null, null, "DAY"));
    }
}

