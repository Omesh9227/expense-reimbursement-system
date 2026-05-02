package com.ps.asde.expense_service.mapper;

import com.ps.asde.expense_service.dto.ReimbursementRequestDTO;
import com.ps.asde.expense_service.dto.ReimbursementResponseDTO;
import com.ps.asde.expense_service.enums.Status;
import com.ps.asde.expense_service.model.Reimbursement;

public class ReimbursementMapper {

    public static Reimbursement toEntity(ReimbursementRequestDTO dto) {
        if (dto == null) return null;
        return Reimbursement.builder()
                .employeeId(dto.getEmployeeId())
                .managerId(dto.getManagerId())
                .amount(dto.getAmount())
                .description(dto.getDescription().trim())
                .status(Status.PENDING)
                .build();
    }

    public static ReimbursementResponseDTO toDTO(Reimbursement r) {
        if (r == null) return null;
        return ReimbursementResponseDTO.builder()
                .id(r.getId())
                .employeeId(r.getEmployeeId())
                .managerId(r.getManagerId())
                .amount(r.getAmount())
                .description(r.getDescription())
                .status(r.getStatus() == null ? null : r.getStatus().name())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .actionAt(r.getActionAt())
                .build();
    }
}
