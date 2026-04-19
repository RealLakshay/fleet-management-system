/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.controller;

import com.fleet.dto.PagedResponse;
import com.fleet.dto.request.CompleteTripRequest;
import com.fleet.dto.request.CreateTripRequest;
import com.fleet.exception.GlobalExceptionHandler;
import com.fleet.dto.response.TripResponse;
import com.fleet.exception.ResourceNotFoundException;
import com.fleet.service.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TripControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private TripService tripService;

    @InjectMocks
    private TripController tripController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tripController)
                                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createTrip_returns201() throws Exception {
        TripResponse response = new TripResponse();
        response.setTripId("t1");

        when(tripService.createTrip(any(CreateTripRequest.class))).thenReturn(response);

        String requestJson = """
                {
                  "vehicleId": "v1",
                  "driverId": "d1",
                  "startDate": "2026-01-10T10:00:00",
                  "purpose": "Delivery",
                  "startLocation": "A",
                  "startOdometer": 100
                }
                """;

        mockMvc.perform(post("/api/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tripId").value("t1"));
    }

    @Test
    void getTrips_returns200WithPagedBody() throws Exception {
        TripResponse trip = new TripResponse();
        trip.setTripId("t1");

        PagedResponse<TripResponse> paged = PagedResponse.<TripResponse>builder()
                .content(List.of(trip))
                .page(0)
                .size(20)
                .totalElements(1)
                .totalPages(1)
                .build();

        when(tripService.getTrips(any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(paged);

        mockMvc.perform(get("/api/trips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].tripId").value("t1"));
    }

    @Test
    void getTripById_valid_returns200() throws Exception {
        TripResponse response = new TripResponse();
        response.setTripId("t1");

        when(tripService.getTripById("t1")).thenReturn(response);

        mockMvc.perform(get("/api/trips/{id}", "t1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tripId").value("t1"));
    }

    @Test
    void getTripById_missing_returns404() throws Exception {
        when(tripService.getTripById("missing"))
                .thenThrow(new ResourceNotFoundException("Trip", "tripId", "missing"));

        mockMvc.perform(get("/api/trips/{id}", "missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deleteTrip_valid_returns200() throws Exception {
        doNothing().when(tripService).deleteTrip(eq("t1"));

        mockMvc.perform(delete("/api/trips/{id}", "t1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void completeTrip_returns200() throws Exception {
        TripResponse response = new TripResponse();
        response.setTripId("t1");

        when(tripService.completeTrip("t1", "Depot", 150L)).thenReturn(response);

        String requestJson = """
                {
                  "endLocation": "Depot",
                  "endOdometer": 150
                }
                """;

        mockMvc.perform(post("/api/trips/{id}/complete", "t1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tripId").value("t1"));
    }
}

