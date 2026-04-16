package com.fleet.service.impl;

// SRP + Low Coupling: expense use-case orchestration stays here and delegates lookup/mapping to collaborators.
import com.fleet.dto.request.CreateExpenseRequest;
import com.fleet.dto.response.ExpenseResponse;
import com.fleet.exception.ResourceNotFoundException;
import com.fleet.mapper.ExpenseMapper;
import com.fleet.model.Expense;
import com.fleet.repository.ExpenseRepository;
import com.fleet.repository.VehicleRepository;
import com.fleet.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final VehicleRepository vehicleRepository;
    private final ExpenseMapper expenseMapper;

    @Override
    @Transactional
    public ExpenseResponse createExpense(CreateExpenseRequest request) {
        vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "vehicleId", request.getVehicleId()));
        Expense expense = expenseMapper.toEntity(request);
        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    @Override
    public ExpenseResponse getExpenseById(String expenseId) {
        return expenseMapper.toResponse(findExpenseById(expenseId));
    }

    @Override
    @Transactional
    public ExpenseResponse updateExpense(String expenseId, CreateExpenseRequest request) {
        Expense existing = findExpenseById(expenseId);
        if (request.getVehicleId() != null) {
            existing.setVehicleId(request.getVehicleId());
        }
        if (request.getExpenseType() != null) {
            existing.setExpenseType(request.getExpenseType());
        }
        if (request.getAmount() != null) {
            existing.setAmount(request.getAmount());
        }
        if (request.getExpenseDate() != null) {
            existing.setExpenseDate(request.getExpenseDate());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getReceiptNumber() != null) {
            existing.setReceiptNumber(request.getReceiptNumber());
        }
        if (request.getVendor() != null) {
            existing.setVendor(request.getVendor());
        }
        return expenseMapper.toResponse(expenseRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteExpense(String expenseId) {
        expenseRepository.delete(findExpenseById(expenseId));
    }

    private Expense findExpenseById(String expenseId) {
        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "expenseId", expenseId));
    }
}