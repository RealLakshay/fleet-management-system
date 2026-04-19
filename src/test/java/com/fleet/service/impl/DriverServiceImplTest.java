/**
 * File purpose: Contains supporting implementation for the Fleet Management application.
 */
package com.fleet.service.impl;

import com.fleet.dto.request.CreateDriverRequest;
import com.fleet.dto.request.UpdateDriverRequest;
import com.fleet.dto.response.DriverResponse;
import com.fleet.exception.ResourceNotFoundException;
import com.fleet.mapper.AssignmentMapper;
import com.fleet.mapper.DriverMapper;
import com.fleet.mapper.TripMapper;
import com.fleet.model.Driver;
import com.fleet.model.enums.AssignmentStatus;
import com.fleet.repository.AssignmentRepository;
import com.fleet.repository.DriverRepository;
import com.fleet.repository.TripRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverServiceImplTest {

    @Mock
    private DriverRepository driverRepository;
    @Mock
    private AssignmentRepository assignmentRepository;
    @Mock
    private TripRepository tripRepository;
    @Mock
    private DriverMapper driverMapper;
    @Mock
    private AssignmentMapper assignmentMapper;
    @Mock
    private TripMapper tripMapper;

    @InjectMocks
    private DriverServiceImpl driverService;

    @Test
    void createDriver_happyPath() {
        CreateDriverRequest request = new CreateDriverRequest();
        request.setLicenseNumber("DL-100");
        request.setEmail("driver@example.com");

        Driver entity = new Driver();
        Driver saved = new Driver();
        saved.setDriverId("d1");
        DriverResponse expected = new DriverResponse();
        expected.setDriverId("d1");

        when(driverRepository.existsByLicenseNumber("DL-100")).thenReturn(false);
        when(driverRepository.existsByEmail("driver@example.com")).thenReturn(false);
        when(driverMapper.toEntity(request)).thenReturn(entity);
        when(driverRepository.save(entity)).thenReturn(saved);
        when(driverMapper.toResponse(saved)).thenReturn(expected);

        DriverResponse actual = driverService.createDriver(request);
        assertEquals("d1", actual.getDriverId());
    }

    @Test
    void getDriverById_happyPath() {
        Driver driver = new Driver();
        driver.setDriverId("d1");
        DriverResponse expected = new DriverResponse();
        expected.setDriverId("d1");

        when(driverRepository.findById("d1")).thenReturn(Optional.of(driver));
        when(driverMapper.toResponse(driver)).thenReturn(expected);

        DriverResponse actual = driverService.getDriverById("d1");
        assertEquals("d1", actual.getDriverId());
    }

    @Test
    void getDriverById_unknownId_throwsResourceNotFound() {
        when(driverRepository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> driverService.getDriverById("missing"));
    }

    @Test
    void updateDriver_happyPath() {
        UpdateDriverRequest request = new UpdateDriverRequest();
        request.setFirstName("Updated");

        Driver existing = new Driver();
        existing.setDriverId("d1");
        existing.setLicenseNumber("DL-100");
        existing.setEmail("driver@example.com");

        DriverResponse expected = new DriverResponse();
        expected.setDriverId("d1");

        when(driverRepository.findById("d1")).thenReturn(Optional.of(existing));
        when(driverRepository.save(existing)).thenReturn(existing);
        when(driverMapper.toResponse(existing)).thenReturn(expected);

        DriverResponse actual = driverService.updateDriver("d1", request);

        verify(driverMapper).updateFromRequest(request, existing);
        assertEquals("d1", actual.getDriverId());
    }

    @Test
    void deleteDriver_happyPath() {
        Driver existing = new Driver();
        existing.setDriverId("d1");

        when(driverRepository.findById("d1")).thenReturn(Optional.of(existing));
        when(assignmentRepository.existsByDriverIdAndStatus("d1", AssignmentStatus.ACTIVE)).thenReturn(false);

        driverService.deleteDriver("d1");

        verify(driverRepository).delete(existing);
    }
}

