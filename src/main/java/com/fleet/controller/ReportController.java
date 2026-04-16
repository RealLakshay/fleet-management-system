package com.fleet.controller;

// GRASP Controller: exposes reporting endpoints while the service layer owns aggregation logic.
import com.fleet.dto.ApiResponse;
import com.fleet.dto.response.ReportResponse;
import com.fleet.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/vehicle-usage")
    public ResponseEntity<ApiResponse<List<ReportResponse>>> vehicleUsage(
            @RequestParam String vehicleId,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(required = false, defaultValue = "DAY") String groupBy) {
        return ResponseEntity.ok(ApiResponse.<List<ReportResponse>>builder().success(true).data(reportService.getVehicleUsage(vehicleId, from, to, groupBy)).build());
    }
}