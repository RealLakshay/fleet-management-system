package com.fleet.service;

import com.fleet.dto.PagedResponse;
import com.fleet.dto.request.CompleteTripRequest;
import com.fleet.dto.request.CreateTripRequest;
import com.fleet.dto.response.TripResponse;
import org.springframework.data.domain.Pageable;

public interface TripService {
    /**
     * Creates a new trip after verifying assignment constraints.
     */
    TripResponse createTrip(CreateTripRequest request);

    /**
     * Returns a paged list of trips.
     */
    PagedResponse<TripResponse> getTrips(String vehicleId, String driverId, String purpose, String startDateFrom, String startDateTo, Pageable pageable);

    /**
     * Returns one trip by id.
     */
    TripResponse getTripById(String tripId);

    /**
     * Updates an existing trip.
     */
    TripResponse updateTrip(String tripId, CreateTripRequest request);

    /**
     * Deletes a trip by id.
     */
    void deleteTrip(String tripId);

    /**
     * Marks a trip complete and captures its ending details.
     */
    TripResponse completeTrip(String tripId, String endLocation, Long endOdometer);
}
