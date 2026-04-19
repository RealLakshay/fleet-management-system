package com.fleet.controller;

// GRASP Controller: keeps HTTP orchestration thin and delegates business rules to the service layer.
import com.fleet.dto.ApiResponse;
import com.fleet.dto.request.CreateAssignmentRequest;
import com.fleet.dto.response.AssignmentResponse;
import com.fleet.service.AssignmentService;
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
@RequestMapping("/api/assignments")
public class AssignmentController {
    private final AssignmentService assignmentService;

    /**
     * Creates a new assignment and returns the saved assignment payload.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AssignmentResponse>> createAssignment(@RequestBody CreateAssignmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<AssignmentResponse>builder().success(true).data(assignmentService.createAssignment(request)).build());
    }

    /**
     * Returns a single assignment by its identifier.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssignmentResponse>> getAssignmentById(@PathVariable("id") String assignmentId) {
        return ResponseEntity.ok(ApiResponse.<AssignmentResponse>builder().success(true).data(assignmentService.getAssignmentById(assignmentId)).build());
    }

    /**
     * Updates the assignment identified by the supplied id.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AssignmentResponse>> updateAssignment(@PathVariable("id") String assignmentId, @RequestBody CreateAssignmentRequest request) {
        return ResponseEntity.ok(ApiResponse.<AssignmentResponse>builder().success(true).data(assignmentService.updateAssignment(assignmentId, request)).build());
    }

    /**
     * Deletes the assignment identified by the supplied id.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAssignment(@PathVariable("id") String assignmentId) {
        assignmentService.deleteAssignment(assignmentId);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).build());
    }
}
