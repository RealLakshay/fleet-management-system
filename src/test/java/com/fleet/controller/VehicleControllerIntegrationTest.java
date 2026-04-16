package com.fleet.controller;

import com.fleet.dto.PagedResponse;
import com.fleet.dto.request.CreateVehicleRequest;
import com.fleet.dto.response.VehicleResponse;
import com.fleet.exception.GlobalExceptionHandler;
import com.fleet.exception.ResourceNotFoundException;
import com.fleet.service.VehicleService;
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
class VehicleControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vehicleController)
                                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createVehicle_returns201() throws Exception {
        VehicleResponse response = new VehicleResponse();
        response.setVehicleId("v1");
        response.setRegistrationNumber("KA-01-1234");

        when(vehicleService.createVehicle(any(CreateVehicleRequest.class))).thenReturn(response);

        String requestJson = """
                {
                  "registrationNumber": "KA-01-1234",
                  "vehicleType": "TRUCK",
                  "year": 2022
                }
                """;

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.vehicleId").value("v1"));
    }

    @Test
    void getVehicles_returns200WithPagedBody() throws Exception {
        VehicleResponse vehicle = new VehicleResponse();
        vehicle.setVehicleId("v1");

        PagedResponse<VehicleResponse> paged = PagedResponse.<VehicleResponse>builder()
                .content(List.of(vehicle))
                .page(0)
                .size(20)
                .totalElements(1)
                .totalPages(1)
                .build();

        when(vehicleService.getVehicles(any(), any(), any(), any(Pageable.class))).thenReturn(paged);

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].vehicleId").value("v1"));
    }

    @Test
    void getVehicleById_valid_returns200() throws Exception {
        VehicleResponse response = new VehicleResponse();
        response.setVehicleId("v1");

        when(vehicleService.getVehicleById("v1")).thenReturn(response);

        mockMvc.perform(get("/api/vehicles/{id}", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.vehicleId").value("v1"));
    }

    @Test
    void getVehicleById_missing_returns404() throws Exception {
        when(vehicleService.getVehicleById("missing"))
                .thenThrow(new ResourceNotFoundException("Vehicle", "vehicleId", "missing"));

        mockMvc.perform(get("/api/vehicles/{id}", "missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deleteVehicle_valid_returns200() throws Exception {
        doNothing().when(vehicleService).deleteVehicle(eq("v1"));

        mockMvc.perform(delete("/api/vehicles/{id}", "v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
