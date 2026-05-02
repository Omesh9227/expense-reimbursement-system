package com.ps.asde.expense_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReimbursementRequestDTO {

    @NotNull(message = "employeeId is required")
    private Long employeeId;

    @NotNull(message = "managerId is required")
    private Long managerId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "description is required")
    private String description;
}
