package com.ps.asde.auth_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String username;
    private String password;
    private String role;
    private Long employeeId;
    private Long managerId;
}
