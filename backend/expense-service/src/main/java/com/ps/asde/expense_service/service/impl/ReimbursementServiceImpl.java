package com.ps.asde.expense_service.service.impl;

import com.ps.asde.expense_service.dto.ReimbursementRequestDTO;
import com.ps.asde.expense_service.dto.ReimbursementResponseDTO;
import com.ps.asde.expense_service.enums.Status;
import com.ps.asde.expense_service.exception.ResourceNotFoundException;
import com.ps.asde.expense_service.mapper.ReimbursementMapper;
import com.ps.asde.expense_service.model.Reimbursement;
import com.ps.asde.expense_service.repository.ReimbursementRepository;
import com.ps.asde.expense_service.service.ReimbursementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReimbursementServiceImpl implements ReimbursementService {

    private final ReimbursementRepository repository;

    @Override
    public ReimbursementResponseDTO create(ReimbursementRequestDTO request) {
        if (request.getEmployeeId().equals(request.getManagerId())) {
            throw new IllegalArgumentException("employeeId and managerId cannot be the same");
        }
        Reimbursement entity = ReimbursementMapper.toEntity(request);
        Reimbursement saved = repository.save(entity);
        return ReimbursementMapper.toDTO(saved);
    }

    @Override
    public ReimbursementResponseDTO getById(Long id) {
        Reimbursement r = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reimbursement with id " + id + " not found"));
        return ReimbursementMapper.toDTO(r);
    }

    @Override
    public List<ReimbursementResponseDTO> listByEmployee(Long employeeId) {
        return repository.findByEmployeeId(employeeId).stream()
                .map(ReimbursementMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ReimbursementResponseDTO> listByManager(Long managerId) {
        return repository.findByManagerId(managerId).stream()
                .map(ReimbursementMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ReimbursementResponseDTO> listAll() {
        return repository.findAll().stream()
                .map(ReimbursementMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public ReimbursementResponseDTO approve(Long id, Long managerId) {
        Reimbursement r = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reimbursement with id " + id + " not found"));
        if (!r.getManagerId().equals(managerId)) {
            throw new IllegalArgumentException("Only the assigned manager can approve this reimbursement");
        }
        if (r.getStatus() != Status.PENDING) {
            throw new IllegalArgumentException("Only PENDING reimbursements can be approved");
        }
        r.setStatus(Status.APPROVED);
        r.setActionAt(LocalDateTime.now());
        Reimbursement saved = repository.save(r);
        return ReimbursementMapper.toDTO(saved);
    }

    @Override
    public ReimbursementResponseDTO reject(Long id, Long managerId) {
        Reimbursement r = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reimbursement with id " + id + " not found"));
        if (!r.getManagerId().equals(managerId)) {
            throw new IllegalArgumentException("Only the assigned manager can reject this reimbursement");
        }
        if (r.getStatus() != Status.PENDING) {
            throw new IllegalArgumentException("Only PENDING reimbursements can be rejected");
        }
        r.setStatus(Status.REJECTED);
        r.setActionAt(LocalDateTime.now());
        Reimbursement saved = repository.save(r);
        return ReimbursementMapper.toDTO(saved);
    }

    @Override
    public void delete(Long id, Long actorId) {
        Reimbursement r = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reimbursement with id " + id + " not found"));

        if (actorId.equals(r.getEmployeeId())) {
            if (r.getStatus() != Status.PENDING) {
                throw new IllegalArgumentException("Employee can delete only PENDING reimbursements");
            }
            repository.delete(r);
            return;
        }

        if (actorId.equals(r.getManagerId())) {
            throw new IllegalArgumentException("Manager deletion is not allowed. Use reject/approve actions.");
        }

        throw new IllegalArgumentException("Only the owner employee or assigned manager (for actions) can perform this operation");
    }
}
