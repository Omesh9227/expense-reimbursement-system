package com.ps.asde.expense_service.controller;

import com.ps.asde.expense_service.dto.ReimbursementRequestDTO;
import com.ps.asde.expense_service.dto.ReimbursementResponseDTO;
import com.ps.asde.expense_service.service.ReimbursementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/reimbursements")
@RequiredArgsConstructor
public class ExpenseController {

    private final ReimbursementService reimbursementService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ReimbursementRequestDTO req, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.toString());
        }
        ReimbursementResponseDTO created = reimbursementService.create(req);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReimbursementResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reimbursementService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ReimbursementResponseDTO>> list(
            @RequestParam(name = "employeeId", required = false) Long employeeId,
            @RequestParam(name = "managerId", required = false) Long managerId) {
        if (employeeId != null) {
            return ResponseEntity.ok(reimbursementService.listByEmployee(employeeId));
        } else if (managerId != null) {
            return ResponseEntity.ok(reimbursementService.listByManager(managerId));
        } else {
            return ResponseEntity.ok(reimbursementService.listAll());
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ReimbursementResponseDTO> approve(@PathVariable Long id,
                                                            HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        Long actingManagerId = (Long) request.getAttribute("managerId");

        if (!"MANAGER".equals(role)) {
            return ResponseEntity.status(403).body(null); // Forbidden
        }

        ReimbursementResponseDTO reimbursement = reimbursementService.getById(id);

        if (!reimbursement.getManagerId().equals(actingManagerId)) {
            return ResponseEntity.status(403).body(null); // Forbidden
        }

        ReimbursementResponseDTO approved = reimbursementService.approve(id, actingManagerId);
        return ResponseEntity.ok(approved);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ReimbursementResponseDTO> reject(@PathVariable Long id,
                                                           HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        Long actingManagerId = (Long) request.getAttribute("managerId");

        if (!"MANAGER".equals(role)) {
            return ResponseEntity.status(403).body(null); // Forbidden
        }

        ReimbursementResponseDTO reimbursement = reimbursementService.getById(id);

        if (!reimbursement.getManagerId().equals(actingManagerId)) {
            return ResponseEntity.status(403).body(null); // Forbidden
        }

        ReimbursementResponseDTO rejected = reimbursementService.reject(id, actingManagerId);
        return ResponseEntity.ok(rejected);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestParam("actorId") Long actorId) {
        reimbursementService.delete(id, actorId);
        return ResponseEntity.noContent().build();
    }
}