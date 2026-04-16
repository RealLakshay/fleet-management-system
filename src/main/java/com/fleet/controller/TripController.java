package com.fleet.controller;

// GRASP Controller: keeps trip endpoint handling thin so trip rules remain in TripServiceImpl.
import com.fleet.dto.ApiResponse;
import com.fleet.dto.PagedResponse;
import com.fleet.dto.request.CompleteTripRequest;
import com.fleet.dto.request.CreateTripRequest;
import com.fleet.dto.response.TripResponse;
import com.fleet.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class TripController {
    private final TripService tripService;

    @PostMapping
    public ResponseEntity<ApiResponse<TripResponse>> createTrip(@RequestBody CreateTripRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<TripResponse>builder().success(true).data(tripService.createTrip(request)).build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<TripResponse>>> getTrips(
            @RequestParam(required = false) String vehicleId,
            @RequestParam(required = false) String driverId,
            @RequestParam(required = false) String purpose,
            @RequestParam(required = false) String startDateFrom,
            @RequestParam(required = false) String startDateTo,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.<PagedResponse<TripResponse>>builder().success(true).data(tripService.getTrips(vehicleId, driverId, purpose, startDateFrom, startDateTo, pageable)).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TripResponse>> getTripById(@PathVariable("id") String tripId) {
        return ResponseEntity.ok(ApiResponse.<TripResponse>builder().success(true).data(tripService.getTripById(tripId)).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TripResponse>> updateTrip(@PathVariable("id") String tripId, @RequestBody CreateTripRequest request) {
        return ResponseEntity.ok(ApiResponse.<TripResponse>builder().success(true).data(tripService.updateTrip(tripId, request)).build());
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<TripResponse>> completeTrip(@PathVariable("id") String tripId, @RequestBody CompleteTripRequest request) {
        return ResponseEntity.ok(ApiResponse.<TripResponse>builder().success(true).data(tripService.completeTrip(tripId, request.getEndLocation(), request.getEndOdometer())).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTrip(@PathVariable("id") String tripId) {
        tripService.deleteTrip(tripId);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).build());
    }
}