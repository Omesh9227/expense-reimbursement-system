package com.ps.asde.employee_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequestDTO {

    @NotBlank(message = "Username is required")
    private String username;

    @NotNull(message = "employeeId is required")
    private Long employeeId;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Role is required")
    private String role;

    @NotNull(message = "ManagerId is required")
    private Long managerId;
}
