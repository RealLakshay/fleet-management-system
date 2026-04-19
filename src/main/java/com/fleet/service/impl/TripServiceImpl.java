package com.fleet.service.impl;

// SRP + Information Expert: trip completion, validation, and distance calculation stay in one use-case service.
import com.fleet.dto.PagedResponse;
import com.fleet.dto.request.CreateTripRequest;
import com.fleet.dto.response.TripResponse;
import com.fleet.exception.BusinessRuleException;
import com.fleet.exception.ResourceNotFoundException;
import com.fleet.mapper.TripMapper;
import com.fleet.model.Assignment;
import com.fleet.model.Driver;
import com.fleet.model.Trip;
import com.fleet.model.Vehicle;
import com.fleet.model.enums.AssignmentStatus;
import com.fleet.repository.AssignmentRepository;
import com.fleet.repository.DriverRepository;
import com.fleet.repository.TripRepository;
import com.fleet.repository.VehicleRepository;
import com.fleet.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripServiceImpl implements TripService {
    private final TripRepository tripRepository;
    private final AssignmentRepository assignmentRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final TripMapper tripMapper;

    /**
     * SRP + INFORMATION EXPERT (GRASP): Trip creation use-case stays in TripService 
     * because trip creation knowledge belongs here.
     * DIP: Depends on abstractions (TripService interface, repositories) not concrete implementations.
     */
    @Override
    @Transactional
    public TripResponse createTrip(CreateTripRequest request) {
        ensureActiveAssignment(request.getVehicleId(), request.getDriverId());
        Trip trip = tripMapper.toEntity(request);
        return tripMapper.toResponse(tripRepository.save(trip));
    }

    /**
     * SRP: Returns a paged list of trips.
     * ISP: This method is part of TripService interface, clients use only what they need.
     */
    @Override
    public PagedResponse<TripResponse> getTrips(String vehicleId, String driverId, String purpose, String startDateFrom, String startDateTo, Pageable pageable) {
        Page<Trip> page = tripRepository.findAll(pageable);
        return toPagedResponse(page, tripMapper::toResponse);
    }

    /**
     * SRP: Single operation - retrieve one trip by id.
     */
    @Override
    public TripResponse getTripById(String tripId) {
        return tripMapper.toResponse(validateAndFetchTrip(tripId));
    }

    /**
     * OCP: Update only fields provided in request.
     * SRP: Update stays separate from creation to keep change logic isolated.
     */
    @Override
    @Transactional
    public TripResponse updateTrip(String tripId, CreateTripRequest request) {
        Trip existing = validateAndFetchTrip(tripId);
        ensureActiveAssignment(request.getVehicleId(), request.getDriverId());
        if (request.getVehicleId() != null) {
            existing.setVehicleId(request.getVehicleId());
        }
        if (request.getDriverId() != null) {
            existing.setDriverId(request.getDriverId());
        }
        if (request.getStartDate() != null) {
            existing.setStartDate(request.getStartDate());
        }
        if (request.getPurpose() != null) {
            existing.setPurpose(request.getPurpose());
        }
        if (request.getStartLocation() != null) {
            existing.setStartLocation(request.getStartLocation());
        }
        if (request.getEndLocation() != null) {
            existing.setEndLocation(request.getEndLocation());
        }
        if (request.getStartOdometer() != null) {
            existing.setStartOdometer(request.getStartOdometer());
        }
        if (request.getEndOdometer() != null) {
            existing.setEndOdometer(request.getEndOdometer());
        }
        if (existing.getStartOdometer() != null && existing.getEndOdometer() != null) {
            existing.setDistanceCovered(BigDecimal.valueOf(existing.getEndOdometer() - existing.getStartOdometer()));
        }
        return tripMapper.toResponse(tripRepository.save(existing));
    }

    /**
     * SRP: Single operation - delete a trip.
     */
    @Override
    @Transactional
    public void deleteTrip(String tripId) {
        tripRepository.delete(validateAndFetchTrip(tripId));
    }

    /**
     * FACADE PATTERN: completeTrip orchestrates multi-step trip completion workflow.
     * Combines validation, distance calculation, and persistence into a single coherent operation.
     * Why: Hides complexity of trip completion (end location, odometer, timestamp, distance) behind one simple call.
     * This keeps the controller thin and makes the use case clear.
     * 
     * Marks the trip as complete and captures end location and odometer values.
     */
    @Override
    @Transactional
    public TripResponse completeTrip(String tripId, String endLocation, Long endOdometer) {
        // PROXY PATTERN: validateAndFetchTrip acts as a controlled access wrapper.
        // Why: Adds validation before repository access, ensuring consistent error handling.
        Trip trip = validateAndFetchTrip(tripId);
        
        // CHAIN OF RESPONSIBILITY: validation steps before completion
        validateTripCompletionData(endLocation, endOdometer);
        
        // Perform completion
        trip.setEndLocation(endLocation);
        trip.setEndOdometer(endOdometer);
        trip.setEndDate(LocalDateTime.now());
        
        // ADAPTER PATTERN: normalizeDistance adapts different distance representations (odometer diff, explicit distance)
        // into one standard distance value.
        trip.setDistanceCovered(normalizeDistance(trip.getStartOdometer(), endOdometer));
        
        return tripMapper.toResponse(tripRepository.save(trip));
    }
    
    /**
     * PROXY PATTERN (Private Controlled Access): Wraps repository access with validation logic.
     * Why: Ensures every trip lookup also confirms the trip exists before use.
     */
    private Trip validateAndFetchTrip(String tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "tripId", tripId));
    }
    
    /**
     * CHAIN OF RESPONSIBILITY PATTERN: Sequential validation checks before trip completion.
     * Why: Each validation rule is independent and can be added/removed without changing other rules.
     */
    private void validateTripCompletionData(String endLocation, Long endOdometer) {
        if (endLocation == null || endLocation.isBlank()) {
            throw new BusinessRuleException("End location is required to complete a trip");
        }
        if (endOdometer == null || endOdometer < 0) {
            throw new BusinessRuleException("End odometer must be a valid non-negative number");
        }
    }
    
    /**
     * ADAPTER PATTERN: Normalizes different distance representations into one consistent BigDecimal distance.
     * Why: Handles cases where distance comes from odometer difference or explicit field,
     * adapting both into a single standard representation.
     */
    private BigDecimal normalizeDistance(Long startOdometer, Long endOdometer) {
        if (startOdometer != null && endOdometer != null) {
            return BigDecimal.valueOf(endOdometer - startOdometer);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Ensures the trip references an existing vehicle and driver with an active assignment.
     */
    private void ensureActiveAssignment(String vehicleId, String driverId) {
        vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "vehicleId", vehicleId));
        driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "driverId", driverId));
        if (!assignmentRepository.existsByVehicleIdAndDriverIdAndStatus(vehicleId, driverId, AssignmentStatus.ACTIVE)) {
            throw new BusinessRuleException("Trip requires an active assignment");
        }
    }

    /**
     * Converts a Spring Data page into the shared paged response structure.
     */
    private <T, R> PagedResponse<R> toPagedResponse(Page<T> page, Function<T, R> mapper) {
        return PagedResponse.<R>builder()
                .content(page.getContent().stream().map(mapper).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
