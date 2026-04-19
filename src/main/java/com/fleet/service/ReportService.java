package com.fleet.service;

import com.fleet.dto.response.ReportResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {
    /**
     * Aggregates usage data for a vehicle over the requested range.
     */
    List<ReportResponse> getVehicleUsage(String vehicleId, LocalDateTime from, LocalDateTime to, String groupBy);
}
