package com.ps.asde.expense_service.repository;

import com.ps.asde.expense_service.model.Reimbursement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReimbursementRepository extends JpaRepository<Reimbursement, Long> {
    List<Reimbursement> findByEmployeeId(Long employeeId);
    List<Reimbursement> findByManagerId(Long managerId);
}
