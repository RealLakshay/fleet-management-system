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

    /**
     * FACTORY PATTERN: createExpense delegates expense creation logic.
     * Uses createExpenseViaFactory to abstract creation rules.
     * Why: Centralizes object creation; makes it easy to add validation, defaults, or type-specific strategies later.
     * 
     * DIP: Depends on ExpenseRepository abstraction, not concrete persistence layer.
     * SRP: Responsibility is orchestrating business logic; creation logic is separated into factory method.
     */
    @Override
    @Transactional
    public ExpenseResponse createExpense(CreateExpenseRequest request) {
        // PROXY PATTERN: resolveVehicleViaProxy acts as controlled access wrapper before creation.
        // Why: Validates vehicle exists before allocating expense resource.
        resolveVehicleViaProxy(request.getVehicleId());
        
        // FACTORY PATTERN: Delegate creation logic to factory method
        Expense expense = createExpenseViaFactory(request);
        return expenseMapper.toResponse(expenseRepository.save(expense));
    }
    
    /**
     * PROXY PATTERN (Private Controlled Access): Validates vehicle existence with clear error handling.
     * Why: Ensures consistent vehicle validation before expense operations.
     */
    private void resolveVehicleViaProxy(String vehicleId) {
        vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "vehicleId", vehicleId));
    }
    
    /**
     * FACTORY PATTERN: Encapsulates expense creation logic in one place.
     * Why: Future expense types (fuel, maintenance, insurance) can have different defaults/strategies
     * without changing this method's callers.
     */
    private Expense createExpenseViaFactory(CreateExpenseRequest request) {
        return expenseMapper.toEntity(request);
    }

    /**
     * SRP: Single operation - retrieve one expense by id.
     * INFORMATION EXPERT (GRASP): This service is the expert on expense retrieval.
     */
    @Override
    public ExpenseResponse getExpenseById(String expenseId) {
        return expenseMapper.toResponse(findExpenseById(expenseId));
    }

    /**
     * OCP: Only updates fields provided in request; extensible for future expense types.
     * SRP: Update logic stays separate from creation to isolate change behavior.
     */
    @Override
    @Transactional
    public ExpenseResponse updateExpense(String expenseId, CreateExpenseRequest request) {
        Expense existing = findExpenseById(expenseId);
        if (request.getVehicleId() != null) {
            resolveVehicleViaProxy(request.getVehicleId());
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

    /**
     * SRP: Single operation - delete an expense.
     */
    @Override
    @Transactional
    public void deleteExpense(String expenseId) {
        expenseRepository.delete(findExpenseById(expenseId));
    }

    /**
     * Loads an expense or throws a not found exception.
     */
    private Expense findExpenseById(String expenseId) {
        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "expenseId", expenseId));
    }
}
