package com.fleet.service.impl;

// SRP + Information Expert: driver lifecycle rules stay here, while repositories and mappers handle persistence and conversion.
import com.fleet.dto.request.CreateDriverRequest;
import com.fleet.dto.request.UpdateDriverRequest;
import com.fleet.dto.response.AssignmentResponse;
import com.fleet.dto.response.DriverResponse;
import com.fleet.dto.response.TripResponse;
import com.fleet.exception.BusinessRuleException;
import com.fleet.exception.ResourceNotFoundException;
import com.fleet.mapper.AssignmentMapper;
import com.fleet.mapper.DriverMapper;
import com.fleet.mapper.TripMapper;
import com.fleet.model.Driver;
import com.fleet.model.enums.AssignmentStatus;
import com.fleet.repository.AssignmentRepository;
import com.fleet.repository.DriverRepository;
import com.fleet.repository.TripRepository;
import com.fleet.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverServiceImpl implements DriverService {
    private final DriverRepository driverRepository;
    private final AssignmentRepository assignmentRepository;
    private final TripRepository tripRepository;
    private final DriverMapper driverMapper;
    private final AssignmentMapper assignmentMapper;
    private final TripMapper tripMapper;

    /**
     * Creates a driver if the license number and email are both unique.
     */
    @Override
    @Transactional
    public DriverResponse createDriver(CreateDriverRequest request) {
        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber()) || driverRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Driver license number or email already exists");
        }

        Driver driver = driverMapper.toEntity(request);
        return driverMapper.toResponse(driverRepository.save(driver));
    }

    /**
     * Returns one driver by id.
     */
    @Override
    public DriverResponse getDriverById(String driverId) {
        return driverMapper.toResponse(findDriverById(driverId));
    }

    /**
     * Updates an existing driver.
     */
    @Override
    @Transactional
    public DriverResponse updateDriver(String driverId, UpdateDriverRequest request) {
        Driver existing = findDriverById(driverId);
        driverMapper.updateFromRequest(request, existing);
        return driverMapper.toResponse(driverRepository.save(existing));
    }

    /**
     * Deletes a driver unless the driver still has an active assignment.
     */
    @Override
    @Transactional
    public void deleteDriver(String driverId) {
        Driver driver = findDriverById(driverId);
        if (assignmentRepository.existsByDriverIdAndStatus(driverId, AssignmentStatus.ACTIVE)) {
            throw new BusinessRuleException("Cannot delete driver with active assignments");
        }
        driverRepository.delete(driver);
    }

    /**
     * Returns the driver's assignments as a paged response.
     */
    @Override
    public Object getDriverAssignments(String driverId, Pageable pageable) {
        findDriverById(driverId);
        return toPagedResponse(assignmentRepository.findByDriverId(driverId, pageable), assignmentMapper::toResponse);
    }

    /**
     * Returns the driver's trips as a paged response.
     */
    @Override
    public Object getDriverTrips(String driverId, Pageable pageable) {
        findDriverById(driverId);
        return toPagedResponse(tripRepository.findByDriverId(driverId, pageable), tripMapper::toResponse);
    }

    /**
     * Loads a driver or throws a not found exception.
     */
    private Driver findDriverById(String driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "driverId", driverId));
    }

    /**
     * Converts a Spring Data page into the shared paged response structure.
     */
    private <T, R> com.fleet.dto.PagedResponse<R> toPagedResponse(Page<T> page, Function<T, R> mapper) {
        return com.fleet.dto.PagedResponse.<R>builder()
                .content(page.getContent().stream().map(mapper).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
