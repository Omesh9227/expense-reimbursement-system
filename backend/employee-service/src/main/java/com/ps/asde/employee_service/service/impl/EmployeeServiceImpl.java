package com.ps.asde.employee_service.service.impl;

import com.ps.asde.employee_service.dto.EmployeeRequestDTO;
import com.ps.asde.employee_service.dto.EmployeeResponseDTO;
import com.ps.asde.employee_service.exception.ResourceNotFoundException;
import com.ps.asde.employee_service.mapper.EmployeeMapper;
import com.ps.asde.employee_service.model.Employee;
import com.ps.asde.employee_service.repository.EmployeeRepository;
import com.ps.asde.employee_service.service.EmployeeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;

    @Override
    public EmployeeResponseDTO create(EmployeeRequestDTO request) {
        Employee entity = EmployeeMapper.toEntity(request);
        Employee saved = repository.save(entity);
        return EmployeeMapper.toDTO(saved);
    }

    @Override
    public EmployeeResponseDTO getById(Long id) {
        Employee e = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with id " + id + " not found"));
        return EmployeeMapper.toDTO(e);
    }

    @Override
    public List<EmployeeResponseDTO> listByEmployee(Long employeeId) {
        return repository.findByEmployeeId(employeeId)
                .stream()
                .map(EmployeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeResponseDTO> listByManager(Long managerId) {
        return repository.findByManagerId(managerId)
                .stream()
                .map(EmployeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeResponseDTO> listAll() {
        return repository.findAll()
                .stream()
                .map(EmployeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Employee e = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with id " + id + " not found"));
        repository.delete(e);
    }
}
