package com.ps.asde.employee_service.mapper;

import com.ps.asde.employee_service.dto.EmployeeRequestDTO;
import com.ps.asde.employee_service.dto.EmployeeResponseDTO;
import com.ps.asde.employee_service.model.Employee;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EmployeeMapper {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static Employee toEntity(EmployeeRequestDTO dto) {
        if (dto == null) return null;

        return Employee.builder()
                .username(dto.getUsername())
                .employeeId(dto.getEmployeeId())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .role(Employee.Role.fromString(dto.getRole()))
                .managerId(dto.getManagerId())
                .build();
    }

    public static EmployeeResponseDTO toDTO(Employee e) {
        if (e == null) return null;

        return EmployeeResponseDTO.builder()
                .id(e.getId())
                .employeeId(e.getEmployeeId())
                .username(e.getUsername())
                .role(e.getRole() == null ? null : e.getRole().name())
                .managerId(e.getManagerId())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
