package com.ps.asde.expense_service.service;

import com.ps.asde.expense_service.dto.ReimbursementRequestDTO;
import com.ps.asde.expense_service.dto.ReimbursementResponseDTO;

import java.util.List;

public interface ReimbursementService {
    ReimbursementResponseDTO create(ReimbursementRequestDTO request);
    ReimbursementResponseDTO getById(Long id);
    List<ReimbursementResponseDTO> listByEmployee(Long employeeId);
    List<ReimbursementResponseDTO> listByManager(Long managerId);
    List<ReimbursementResponseDTO> listAll();
    ReimbursementResponseDTO approve(Long id, Long managerId);
    ReimbursementResponseDTO reject(Long id, Long managerId);
    void delete(Long id, Long actorId);
}
