package com.fleet.service.impl;

import com.fleet.dto.request.CreateExpenseRequest;
import com.fleet.dto.response.ExpenseResponse;
import com.fleet.exception.ResourceNotFoundException;
import com.fleet.mapper.ExpenseMapper;
import com.fleet.model.Expense;
import com.fleet.model.Vehicle;
import com.fleet.model.enums.ExpenseType;
import com.fleet.repository.ExpenseRepository;
import com.fleet.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private ExpenseMapper expenseMapper;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    @Test
    void createExpense_happyPath() {
        CreateExpenseRequest request = new CreateExpenseRequest();
        request.setVehicleId("v1");
        request.setAmount(BigDecimal.TEN);
        request.setExpenseDate(LocalDate.now());
        request.setExpenseType(ExpenseType.FUEL);

        Expense entity = new Expense();
        Expense saved = new Expense();
        saved.setExpenseId("e1");
        ExpenseResponse expected = new ExpenseResponse();
        expected.setExpenseId("e1");

        when(vehicleRepository.findById("v1")).thenReturn(Optional.of(new Vehicle()));
        when(expenseMapper.toEntity(request)).thenReturn(entity);
        when(expenseRepository.save(entity)).thenReturn(saved);
        when(expenseMapper.toResponse(saved)).thenReturn(expected);

        ExpenseResponse actual = expenseService.createExpense(request);
        assertEquals("e1", actual.getExpenseId());
    }

    @Test
    void getExpenseById_happyPath() {
        Expense expense = new Expense();
        expense.setExpenseId("e1");
        ExpenseResponse expected = new ExpenseResponse();
        expected.setExpenseId("e1");

        when(expenseRepository.findById("e1")).thenReturn(Optional.of(expense));
        when(expenseMapper.toResponse(expense)).thenReturn(expected);

        ExpenseResponse actual = expenseService.getExpenseById("e1");
        assertEquals("e1", actual.getExpenseId());
    }

    @Test
    void getExpenseById_unknownId_throwsResourceNotFound() {
        when(expenseRepository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> expenseService.getExpenseById("missing"));
    }

    @Test
    void updateExpense_happyPath() {
        Expense existing = new Expense();
        existing.setExpenseId("e1");

        CreateExpenseRequest request = new CreateExpenseRequest();
        request.setAmount(new BigDecimal("120.50"));
        request.setExpenseDate(LocalDate.now());
        request.setExpenseType(ExpenseType.REPAIR);

        ExpenseResponse expected = new ExpenseResponse();
        expected.setExpenseId("e1");

        when(expenseRepository.findById("e1")).thenReturn(Optional.of(existing));
        when(expenseRepository.save(existing)).thenReturn(existing);
        when(expenseMapper.toResponse(existing)).thenReturn(expected);

        ExpenseResponse actual = expenseService.updateExpense("e1", request);

        assertEquals(new BigDecimal("120.50"), existing.getAmount());
        assertEquals("e1", actual.getExpenseId());
    }

    @Test
    void deleteExpense_happyPath() {
        Expense existing = new Expense();
        existing.setExpenseId("e1");

        when(expenseRepository.findById("e1")).thenReturn(Optional.of(existing));

        expenseService.deleteExpense("e1");

        verify(expenseRepository).delete(existing);
    }
}
