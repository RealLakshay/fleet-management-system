package com.fleet.controller;

// GRASP Controller: keeps transport concerns separate from maintenance business rules.
import com.fleet.dto.ApiResponse;
import com.fleet.dto.request.CreateMaintenanceRequest;
import com.fleet.dto.response.MaintenanceResponse;
import com.fleet.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/maintenance")
public class MaintenanceController {
    private final MaintenanceService maintenanceService;

    @PostMapping
    public ResponseEntity<ApiResponse<MaintenanceResponse>> createMaintenance(@RequestBody CreateMaintenanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<MaintenanceResponse>builder().success(true).data(maintenanceService.createMaintenance(request)).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaintenanceResponse>> getMaintenanceById(@PathVariable("id") String maintenanceId) {
        return ResponseEntity.ok(ApiResponse.<MaintenanceResponse>builder().success(true).data(maintenanceService.getMaintenanceById(maintenanceId)).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MaintenanceResponse>> updateMaintenance(@PathVariable("id") String maintenanceId, @RequestBody CreateMaintenanceRequest request) {
        return ResponseEntity.ok(ApiResponse.<MaintenanceResponse>builder().success(true).data(maintenanceService.updateMaintenance(maintenanceId, request)).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMaintenance(@PathVariable("id") String maintenanceId) {
        maintenanceService.deleteMaintenance(maintenanceId);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).build());
    }
}