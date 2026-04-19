package com.fleet.service.impl;

// SRP + Information Expert: reporting aggregation and bucketing stay here so controllers stay thin.
import com.fleet.dto.response.ReportResponse;
import com.fleet.model.Trip;
import com.fleet.repository.DriverRepository;
import com.fleet.repository.ExpenseRepository;
import com.fleet.repository.TripRepository;
import com.fleet.repository.VehicleRepository;
import com.fleet.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final TripRepository tripRepository;
    private final ExpenseRepository expenseRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    /**
     * Aggregates vehicle trips into period buckets for reporting.
     */
    @Override
    public List<ReportResponse> getVehicleUsage(String vehicleId, LocalDateTime from, LocalDateTime to, String groupBy) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new com.fleet.exception.ResourceNotFoundException("Vehicle", "vehicleId", vehicleId);
        }

        Specification<Trip> specification = Specification.where((root, query, cb) -> cb.equal(root.get("vehicleId"), vehicleId));
        List<Trip> trips = tripRepository.findAll(specification, Sort.by(Sort.Direction.ASC, "startDate"));

        Map<String, Aggregation> aggregated = new LinkedHashMap<>();
        for (Trip trip : trips) {
            if (from != null && trip.getStartDate() != null && trip.getStartDate().isBefore(from)) continue;
            if (to != null && trip.getStartDate() != null && trip.getStartDate().isAfter(to)) continue;

            String bucket = bucketKey(trip, groupBy);
            Aggregation aggregation = aggregated.computeIfAbsent(bucket, ignored -> new Aggregation());
            aggregation.tripCount++;
            BigDecimal distance = trip.getDistanceCovered();
            if (distance == null && trip.getStartOdometer() != null && trip.getEndOdometer() != null) {
                distance = BigDecimal.valueOf(trip.getEndOdometer() - trip.getStartOdometer());
            }
            if (distance != null) {
                aggregation.totalDistance = aggregation.totalDistance.add(distance);
            }
        }

        return aggregated.entrySet().stream()
                .map(entry -> {
                    ReportResponse response = new ReportResponse();
                    response.setPeriod(entry.getKey());
                    response.setTripCount(entry.getValue().tripCount);
                    response.setTotalDistance(entry.getValue().totalDistance);
                    response.setTotalExpense(BigDecimal.ZERO);
                    return response;
                })
                .toList();
    }

    /**
     * Builds the grouping key for a trip based on the requested interval.
     */
    private String bucketKey(Trip trip, String groupBy) {
        if (!StringUtils.hasText(groupBy) || trip.getStartDate() == null) {
            return "ALL";
        }
        LocalDate date = trip.getStartDate().toLocalDate();
        return switch (groupBy.trim().toUpperCase()) {
            case "DAY" -> date.format(DateTimeFormatter.ISO_DATE);
            case "MONTH" -> date.getYear() + "-" + String.format("%02d", date.getMonthValue());
            case "YEAR" -> String.valueOf(date.getYear());
            default -> date.format(DateTimeFormatter.ISO_DATE);
        };
    }

    private static final class Aggregation {
        private long tripCount = 0;
        private BigDecimal totalDistance = BigDecimal.ZERO;
    }
}
