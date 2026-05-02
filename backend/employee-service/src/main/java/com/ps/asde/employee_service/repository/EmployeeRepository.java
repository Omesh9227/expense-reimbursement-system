package com.ps.asde.employee_service.repository;

import com.ps.asde.employee_service.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByManagerId(Long managerId);
    List<Employee> findByEmployeeId(Long employeeId);
}
