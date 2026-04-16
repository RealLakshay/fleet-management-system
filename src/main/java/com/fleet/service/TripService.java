package com.fleet.service;

import com.fleet.dto.PagedResponse;
import com.fleet.dto.request.CompleteTripRequest;
import com.fleet.dto.request.CreateTripRequest;
import com.fleet.dto.response.TripResponse;
import org.springframework.data.domain.Pageable;

public interface TripService {
    TripResponse createTrip(CreateTripRequest request);

    PagedResponse<TripResponse> getTrips(String vehicleId, String driverId, String purpose, String startDateFrom, String startDateTo, Pageable pageable);

    TripResponse getTripById(String tripId);

    TripResponse updateTrip(String tripId, CreateTripRequest request);

    void deleteTrip(String tripId);

    TripResponse completeTrip(String tripId, String endLocation, Long endOdometer);
}