package com.fleet.service.impl;

// SRP + Low Coupling: vehicle lifecycle rules stay here while repositories and mappers handle persistence details.
import com.fleet.dto.PagedResponse;
import com.fleet.dto.request.CreateVehicleRequest;
import com.fleet.dto.request.UpdateVehicleRequest;
import com.fleet.dto.response.AssignmentResponse;
import com.fleet.dto.response.ExpenseResponse;
import com.fleet.dto.response.MaintenanceResponse;
import com.fleet.dto.response.TripResponse;
import com.fleet.dto.response.VehicleResponse;
import com.fleet.exception.BusinessRuleException;
import com.fleet.exception.ResourceNotFoundException;
import com.fleet.mapper.AssignmentMapper;
import com.fleet.mapper.ExpenseMapper;
import com.fleet.mapper.MaintenanceMapper;
import com.fleet.mapper.TripMapper;
import com.fleet.mapper.VehicleMapper;
import com.fleet.model.Vehicle;
import com.fleet.model.enums.AssignmentStatus;
import com.fleet.model.enums.VehicleStatus;
import com.fleet.repository.AssignmentRepository;
import com.fleet.repository.ExpenseRepository;
import com.fleet.repository.MaintenanceRepository;
import com.fleet.repository.TripRepository;
import com.fleet.repository.VehicleRepository;
import com.fleet.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final AssignmentRepository assignmentRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final ExpenseRepository expenseRepository;
    private final TripRepository tripRepository;
    private final VehicleMapper vehicleMapper;
    private final AssignmentMapper assignmentMapper;
    private final MaintenanceMapper maintenanceMapper;
    private final ExpenseMapper expenseMapper;
    private final TripMapper tripMapper;

    /**
     * Creates a new vehicle and defaults its status when none is provided.
     */
    @Override
    @Transactional
    public VehicleResponse createVehicle(CreateVehicleRequest request) {
        if (vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new BusinessRuleException("Vehicle registration number already exists");
        }

        Vehicle vehicle = vehicleMapper.toEntity(request);
        if (vehicle.getStatus() == null) {
            vehicle.setStatus(VehicleStatus.OPERATIONAL);
        }

        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    /**
     * Returns a filtered list of vehicles with simple in-memory search support.
     */
    @Override
    public PagedResponse<VehicleResponse> getVehicles(VehicleStatus status, String vehicleType, String search, Pageable pageable) {
        Page<Vehicle> page = vehicleRepository.findAll(pageable);
        List<VehicleResponse> filtered = page.getContent().stream()
                .filter(vehicle -> status == null || status.equals(vehicle.getStatus()))
                .filter(vehicle -> !StringUtils.hasText(vehicleType) || vehicleType.equalsIgnoreCase(vehicle.getVehicleType()))
                .filter(vehicle -> !StringUtils.hasText(search)
                        || containsIgnoreCase(vehicle.getRegistrationNumber(), search)
                        || containsIgnoreCase(vehicle.getModel(), search))
                .map(vehicleMapper::toResponse)
                .toList();
        return PagedResponse.<VehicleResponse>builder()
                .content(filtered)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(filtered.size())
                .totalPages(1)
                .build();
    }

    /**
     * Returns one vehicle by id.
     */
    @Override
    public VehicleResponse getVehicleById(String vehicleId) {
        return vehicleMapper.toResponse(findVehicleById(vehicleId));
    }

    /**
     * Updates an existing vehicle while preventing duplicate registration numbers.
     */
    @Override
    @Transactional
    public VehicleResponse updateVehicle(String vehicleId, UpdateVehicleRequest request) {
        Vehicle existing = findVehicleById(vehicleId);

        if (StringUtils.hasText(request.getRegistrationNumber())
                && !request.getRegistrationNumber().equalsIgnoreCase(existing.getRegistrationNumber())
                && vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new BusinessRuleException("Vehicle registration number already exists");
        }

        vehicleMapper.updateFromRequest(request, existing);
        return vehicleMapper.toResponse(vehicleRepository.save(existing));
    }

    /**
     * Deletes a vehicle unless it still has active assignments.
     */
    @Override
    @Transactional
    public void deleteVehicle(String vehicleId) {
        Vehicle vehicle = findVehicleById(vehicleId);
        if (assignmentRepository.existsByVehicleIdAndStatus(vehicleId, AssignmentStatus.ACTIVE)) {
            throw new BusinessRuleException("Cannot delete vehicle with active assignments");
        }
        vehicleRepository.delete(vehicle);
    }

    /**
     * Returns assignments linked to the selected vehicle.
     */
    @Override
    public PagedResponse<AssignmentResponse> getVehicleAssignments(String vehicleId, Pageable pageable) {
        findVehicleById(vehicleId);
        return toPagedResponse(assignmentRepository.findByVehicleId(vehicleId, pageable), assignmentMapper::toResponse);
    }

    /**
     * Returns maintenance records linked to the selected vehicle.
     */
    @Override
    public PagedResponse<MaintenanceResponse> getVehicleMaintenance(String vehicleId, Pageable pageable) {
        findVehicleById(vehicleId);
        return toPagedResponse(maintenanceRepository.findByVehicleId(vehicleId, pageable), maintenanceMapper::toResponse);
    }

    /**
     * Returns expenses linked to the selected vehicle.
     */
    @Override
    public PagedResponse<ExpenseResponse> getVehicleExpenses(String vehicleId, Pageable pageable) {
        findVehicleById(vehicleId);
        return toPagedResponse(expenseRepository.findByVehicleId(vehicleId, pageable), expenseMapper::toResponse);
    }

    /**
     * Returns trips linked to the selected vehicle.
     */
    @Override
    public PagedResponse<TripResponse> getVehicleTrips(String vehicleId, Pageable pageable) {
        findVehicleById(vehicleId);
        return toPagedResponse(tripRepository.findByVehicleId(vehicleId, pageable), tripMapper::toResponse);
    }

    /**
     * Loads a vehicle or throws a not found exception.
     */
    private Vehicle findVehicleById(String vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "vehicleId", vehicleId));
    }

    /**
     * Performs a case-insensitive contains check while guarding against null values.
     */
    private boolean containsIgnoreCase(String value, String search) {
        return value != null && value.toLowerCase().contains(search.toLowerCase());
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
