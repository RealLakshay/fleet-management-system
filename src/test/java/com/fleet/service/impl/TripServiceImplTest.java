package com.fleet.service.impl;

import com.fleet.dto.request.CreateTripRequest;
import com.fleet.dto.response.TripResponse;
import com.fleet.exception.BusinessRuleException;
import com.fleet.exception.ResourceNotFoundException;
import com.fleet.mapper.TripMapper;
import com.fleet.model.Trip;
import com.fleet.model.Vehicle;
import com.fleet.model.enums.AssignmentStatus;
import com.fleet.repository.AssignmentRepository;
import com.fleet.repository.DriverRepository;
import com.fleet.repository.TripRepository;
import com.fleet.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripServiceImplTest {

    @Mock
    private TripRepository tripRepository;
    @Mock
    private AssignmentRepository assignmentRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private TripMapper tripMapper;

    @InjectMocks
    private TripServiceImpl tripService;

    @Test
    void createTrip_happyPath() {
        CreateTripRequest request = new CreateTripRequest();
        request.setVehicleId("v1");
        request.setDriverId("d1");
        request.setStartDate(LocalDateTime.now());
        request.setPurpose("Delivery");
        request.setStartLocation("A");
        request.setStartOdometer(100L);

        Trip entity = new Trip();
        Trip saved = new Trip();
        saved.setTripId("t1");
        TripResponse expected = new TripResponse();
        expected.setTripId("t1");

        when(vehicleRepository.findById("v1")).thenReturn(Optional.of(new Vehicle()));
        when(driverRepository.findById("d1")).thenReturn(Optional.of(new com.fleet.model.Driver()));
        when(assignmentRepository.existsByVehicleIdAndDriverIdAndStatus("v1", "d1", AssignmentStatus.ACTIVE)).thenReturn(true);
        when(tripMapper.toEntity(request)).thenReturn(entity);
        when(tripRepository.save(entity)).thenReturn(saved);
        when(tripMapper.toResponse(saved)).thenReturn(expected);

        TripResponse actual = tripService.createTrip(request);
        assertEquals("t1", actual.getTripId());
    }

    @Test
    void createTrip_withoutActiveAssignment_throwsBusinessRuleException() {
        CreateTripRequest request = new CreateTripRequest();
        request.setVehicleId("v1");
        request.setDriverId("d1");
        request.setStartDate(LocalDateTime.now());

        when(vehicleRepository.findById("v1")).thenReturn(Optional.of(new Vehicle()));
        when(driverRepository.findById("d1")).thenReturn(Optional.of(new com.fleet.model.Driver()));
        when(assignmentRepository.existsByVehicleIdAndDriverIdAndStatus("v1", "d1", AssignmentStatus.ACTIVE)).thenReturn(false);

        assertThrows(BusinessRuleException.class, () -> tripService.createTrip(request));
    }

    @Test
    void getTripById_happyPath() {
        Trip trip = new Trip();
        trip.setTripId("t1");
        TripResponse expected = new TripResponse();
        expected.setTripId("t1");

        when(tripRepository.findById("t1")).thenReturn(Optional.of(trip));
        when(tripMapper.toResponse(trip)).thenReturn(expected);

        TripResponse actual = tripService.getTripById("t1");
        assertEquals("t1", actual.getTripId());
    }

    @Test
    void getTripById_unknownId_throwsResourceNotFound() {
        when(tripRepository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> tripService.getTripById("missing"));
    }

    @Test
    void updateTrip_happyPath() {
        Trip existing = new Trip();
        existing.setTripId("t1");

        CreateTripRequest request = new CreateTripRequest();
        request.setVehicleId("v1");
        request.setDriverId("d1");
        request.setStartDate(LocalDateTime.now());
        request.setPurpose("Updated");
        request.setStartLocation("S");
        request.setEndLocation("E");
        request.setStartOdometer(120L);

        TripResponse expected = new TripResponse();
        expected.setTripId("t1");

        when(tripRepository.findById("t1")).thenReturn(Optional.of(existing));
        when(vehicleRepository.findById("v1")).thenReturn(Optional.of(new Vehicle()));
        when(driverRepository.findById("d1")).thenReturn(Optional.of(new com.fleet.model.Driver()));
        when(assignmentRepository.existsByVehicleIdAndDriverIdAndStatus("v1", "d1", AssignmentStatus.ACTIVE)).thenReturn(true);
        when(tripRepository.save(existing)).thenReturn(existing);
        when(tripMapper.toResponse(existing)).thenReturn(expected);

        TripResponse actual = tripService.updateTrip("t1", request);

        assertEquals("Updated", existing.getPurpose());
        assertEquals("t1", actual.getTripId());
    }

    @Test
    void deleteTrip_happyPath() {
        Trip existing = new Trip();
        existing.setTripId("t1");

        when(tripRepository.findById("t1")).thenReturn(Optional.of(existing));

        tripService.deleteTrip("t1");

        verify(tripRepository).delete(existing);
    }
}
