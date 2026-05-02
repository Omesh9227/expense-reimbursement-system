package com.ps.asde.employee_service.service.impl;

import com.ps.asde.employee_service.dto.EmployeeRequestDTO;
import com.ps.asde.employee_service.dto.EmployeeResponseDTO;
import com.ps.asde.employee_service.model.Employee;
import com.ps.asde.employee_service.repository.EmployeeRepository;
import com.ps.asde.employee_service.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee mockEmployee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockEmployee = Employee.builder()
                .id(1L)
                .username("john.doe")
                .passwordHash("hashed123")
                .role(Employee.Role.EMPLOYEE)
                .managerId(99L)
                .employeeId(123L)
                .build();
    }

    @Test
    @DisplayName("Create employee successfully")
    void createEmployee_Success() {
        EmployeeRequestDTO req = new EmployeeRequestDTO();
        req.setUsername("john.doe");
        req.setPassword("plain123");
        req.setManagerId(99L);

        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        EmployeeResponseDTO result = employeeService.create(req);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("john.doe");
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Get employee by ID successfully")
    void getById_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));

        EmployeeResponseDTO result = employeeService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("List all employees successfully")
    void listAll_Success() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(mockEmployee));

        List<EmployeeResponseDTO> result = employeeService.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("john.doe");
    }

    @Test
    @DisplayName("List employees by manager successfully")
    void listByManager_Success() {
        when(employeeRepository.findByManagerId(99L)).thenReturn(Arrays.asList(mockEmployee));

        List<EmployeeResponseDTO> result = employeeService.listByManager(99L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getManagerId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("Delete employee successfully")
    void deleteEmployee_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));

        employeeService.delete(1L);

        verify(employeeRepository, times(1)).delete(mockEmployee);
    }


    @Test
    @DisplayName("Get employee fails if not found")
    void getById_NotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getById(1L))
                .isInstanceOf(RuntimeException.class) // or custom NotFoundException
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Delete employee fails if not exists")
    void deleteEmployee_NotFound() {
        when(employeeRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> employeeService.delete(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }
}