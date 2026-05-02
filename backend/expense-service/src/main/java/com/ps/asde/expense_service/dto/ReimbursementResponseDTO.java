package com.ps.asde.expense_service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReimbursementResponseDTO {
    private Long id;
    private Long employeeId;
    private Long managerId;
    private BigDecimal amount;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime actionAt;
}
