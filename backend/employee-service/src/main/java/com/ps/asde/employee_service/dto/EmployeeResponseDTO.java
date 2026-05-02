package com.ps.asde.employee_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponseDTO {
    private Long id;
    private String username;
    private String role;
    private Long managerId;
    private Long employeeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
