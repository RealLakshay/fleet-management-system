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
     * Creates a trip after confirming the vehicle and driver have an active assignment.
     */
    @Override
    @Transactional
    public TripResponse createTrip(CreateTripRequest request) {
        ensureActiveAssignment(request.getVehicleId(), request.getDriverId());
        Trip trip = tripMapper.toEntity(request);
        return tripMapper.toResponse(tripRepository.save(trip));
    }

    /**
     * Returns a paged list of trips.
     */
    @Override
    public PagedResponse<TripResponse> getTrips(String vehicleId, String driverId, String purpose, String startDateFrom, String startDateTo, Pageable pageable) {
        Page<Trip> page = tripRepository.findAll(pageable);
        return toPagedResponse(page, tripMapper::toResponse);
    }

    /**
     * Returns one trip by id.
     */
    @Override
    public TripResponse getTripById(String tripId) {
        return tripMapper.toResponse(findTripById(tripId));
    }

    /**
     * Updates an existing trip and recalculates the trip distance when possible.
     */
    @Override
    @Transactional
    public TripResponse updateTrip(String tripId, CreateTripRequest request) {
        Trip existing = findTripById(tripId);
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
     * Deletes a trip by id.
     */
    @Override
    @Transactional
    public void deleteTrip(String tripId) {
        tripRepository.delete(findTripById(tripId));
    }

    /**
     * Marks the trip as complete and captures end location and odometer values.
     */
    @Override
    @Transactional
    public TripResponse completeTrip(String tripId, String endLocation, Long endOdometer) {
        Trip trip = findTripById(tripId);
        trip.setEndLocation(endLocation);
        trip.setEndOdometer(endOdometer);
        trip.setEndDate(LocalDateTime.now());
        if (trip.getStartOdometer() != null && endOdometer != null) {
            trip.setDistanceCovered(BigDecimal.valueOf(endOdometer - trip.getStartOdometer()));
        }
        return tripMapper.toResponse(tripRepository.save(trip));
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
     * Loads a trip or throws a not found exception.
     */
    private Trip findTripById(String tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "tripId", tripId));
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
