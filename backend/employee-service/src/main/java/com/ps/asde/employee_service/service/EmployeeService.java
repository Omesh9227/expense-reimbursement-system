package com.ps.asde.employee_service.service;

import com.ps.asde.employee_service.dto.EmployeeRequestDTO;
import com.ps.asde.employee_service.dto.EmployeeResponseDTO;
import java.util.List;

public interface EmployeeService {
    EmployeeResponseDTO create(EmployeeRequestDTO request);
    EmployeeResponseDTO getById(Long id);
    List<EmployeeResponseDTO> listByEmployee(Long employeeId);
    List<EmployeeResponseDTO> listByManager(Long managerId);
    List<EmployeeResponseDTO> listAll();
    void delete(Long id);
}
