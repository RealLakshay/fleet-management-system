/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.mapper;

// Pure Fabrication: trip mapping stays isolated so trip logic does not mix with transport DTOs.
import com.fleet.dto.request.CreateTripRequest;
import com.fleet.dto.response.TripResponse;
import com.fleet.model.Trip;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class TripMapper {
    public Trip toEntity(CreateTripRequest request) {
        Trip trip = new Trip();
        trip.setTripId(UUID.randomUUID().toString());
        updateFromRequest(request, trip);
        return trip;
    }

    public TripResponse toResponse(Trip trip) {
        TripResponse response = new TripResponse();
        response.setTripId(trip.getTripId());
        response.setVehicleId(trip.getVehicleId());
        response.setDriverId(trip.getDriverId());
        response.setStartDate(trip.getStartDate());
        response.setEndDate(trip.getEndDate());
        response.setPurpose(trip.getPurpose());
        response.setStartLocation(trip.getStartLocation());
        response.setEndLocation(trip.getEndLocation());
        response.setStartOdometer(trip.getStartOdometer());
        response.setEndOdometer(trip.getEndOdometer());
        response.setDistanceCovered(trip.getDistanceCovered());
        return response;
    }

    public void updateFromRequest(CreateTripRequest request, Trip trip) {
        if (request.getVehicleId() != null) trip.setVehicleId(request.getVehicleId());
        if (request.getDriverId() != null) trip.setDriverId(request.getDriverId());
        if (request.getStartDate() != null) trip.setStartDate(request.getStartDate());
        if (request.getPurpose() != null) trip.setPurpose(request.getPurpose());
        if (request.getStartLocation() != null) trip.setStartLocation(request.getStartLocation());
        if (request.getEndLocation() != null) trip.setEndLocation(request.getEndLocation());
        if (request.getStartOdometer() != null) trip.setStartOdometer(request.getStartOdometer());
        if (request.getEndOdometer() != null) trip.setEndOdometer(request.getEndOdometer());
        if (trip.getStartOdometer() != null && trip.getEndOdometer() != null) {
            trip.setDistanceCovered(BigDecimal.valueOf(trip.getEndOdometer() - trip.getStartOdometer()));
        }
    }
}
