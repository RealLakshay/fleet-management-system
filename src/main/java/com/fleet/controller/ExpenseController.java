package com.fleet.controller;

// GRASP Controller: this layer only coordinates HTTP calls and forwards work to the service layer.
import com.fleet.dto.ApiResponse;
import com.fleet.dto.request.CreateExpenseRequest;
import com.fleet.dto.response.ExpenseResponse;
import com.fleet.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    /**
     * Creates a new expense entry.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(@RequestBody CreateExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ExpenseResponse>builder().success(true).data(expenseService.createExpense(request)).build());
    }

    /**
     * Returns an expense by id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseById(@PathVariable("id") String expenseId) {
        return ResponseEntity.ok(ApiResponse.<ExpenseResponse>builder().success(true).data(expenseService.getExpenseById(expenseId)).build());
    }

    /**
     * Updates the expense identified by the supplied id.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(@PathVariable("id") String expenseId, @RequestBody CreateExpenseRequest request) {
        return ResponseEntity.ok(ApiResponse.<ExpenseResponse>builder().success(true).data(expenseService.updateExpense(expenseId, request)).build());
    }

    /**
     * Deletes the expense identified by the supplied id.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@PathVariable("id") String expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).build());
    }
}
