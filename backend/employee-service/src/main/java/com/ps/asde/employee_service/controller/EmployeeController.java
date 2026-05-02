package com.ps.asde.employee_service.controller;

import com.ps.asde.employee_service.dto.EmployeeRequestDTO;
import com.ps.asde.employee_service.dto.EmployeeResponseDTO;
import com.ps.asde.employee_service.exception.ErrorResponse;
import com.ps.asde.employee_service.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<?> createEmployee(
            @Valid @RequestBody EmployeeRequestDTO request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {

        String role = (String) httpRequest.getAttribute("role");
        Long actingManagerId = Long.valueOf(httpRequest.getAttribute("employeeId").toString());

        if (!"MANAGER".equals(role)) {
            return ResponseEntity.status(403)
                    .body(new ErrorResponse("Forbidden", "Only managers can create employees"));
        }

        if (bindingResult.hasErrors()) {
            ErrorResponse err = new ErrorResponse("Validation failed", bindingResult.toString());
            return ResponseEntity.badRequest().body(err);
        }

        request.setManagerId(actingManagerId);
        EmployeeResponseDTO created = employeeService.create(request);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployee(@PathVariable Long id,
                                                           HttpServletRequest httpRequest) {
        String role = (String) httpRequest.getAttribute("role");
        Long userId = Long.valueOf(httpRequest.getAttribute("employeeId").toString());

        if ("EMPLOYEE".equals(role) && !userId.equals(id)) {
            return ResponseEntity.status(403).body(null);
        }

        EmployeeResponseDTO dto = employeeService.getById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> listEmployees(
            @RequestParam(name = "managerId", required = false) Long managerId,
            HttpServletRequest httpRequest) {

        String role = (String) httpRequest.getAttribute("role");

        if (!"MANAGER".equals(role)) {
            return ResponseEntity.status(403).body(null);
        }

        List<EmployeeResponseDTO> list;
        if (managerId == null) {
            list = employeeService.listAll();
        } else {
            list = employeeService.listByManager(managerId);
        }
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id,
                                            HttpServletRequest httpRequest) {
        String role = (String) httpRequest.getAttribute("role");
        Long actingManagerId = Long.valueOf(httpRequest.getAttribute("employeeId").toString());

        if (!"MANAGER".equals(role)) {
            return ResponseEntity.status(403)
                    .body(new ErrorResponse("Forbidden", "Only managers can delete employees"));
        }

        // Prevent manager from deleting themselves
        if (actingManagerId.equals(id)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid Operation", "Managers cannot delete themselves"));
        }

        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
