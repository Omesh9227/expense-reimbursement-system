package com.ps.asde.employee_service.controller;

import com.ps.asde.employee_service.dto.EmployeeRequestDTO;
import com.ps.asde.employee_service.dto.EmployeeResponseDTO;
import com.ps.asde.employee_service.exception.ErrorResponse;
import com.ps.asde.employee_service.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private EmployeeController controller;

    private EmployeeRequestDTO requestDTO;
    private EmployeeResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requestDTO = new EmployeeRequestDTO();
        responseDTO = new EmployeeResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setUsername("John Doe");
    }

    // ================= CREATE EMPLOYEE =================

    @Test
    void createEmployee_HappyPath_ManagerCreatesEmployee() {
        when(httpRequest.getAttribute("role")).thenReturn("MANAGER");
        when(httpRequest.getAttribute("employeeId")).thenReturn(100L);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(employeeService.create(any(EmployeeRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<?> result = controller.createEmployee(requestDTO, bindingResult, httpRequest);

        assertEquals(200, result.getStatusCodeValue());
        assertTrue(result.getBody() instanceof EmployeeResponseDTO);
        assertEquals("John Doe", ((EmployeeResponseDTO) result.getBody()).getUsername());
        verify(employeeService, times(1)).create(any(EmployeeRequestDTO.class));
    }

    @Test
    void createEmployee_SadPath_NotManager() {
        when(httpRequest.getAttribute("role")).thenReturn("EMPLOYEE");
        when(httpRequest.getAttribute("employeeId")).thenReturn(200L);

        ResponseEntity<?> result = controller.createEmployee(requestDTO, bindingResult, httpRequest);

        assertEquals(403, result.getStatusCodeValue());
        assertTrue(result.getBody() instanceof ErrorResponse);
        verify(employeeService, never()).create(any());
    }

    @Test
    void createEmployee_SadPath_ValidationError() {
        when(httpRequest.getAttribute("role")).thenReturn("MANAGER");
        when(httpRequest.getAttribute("employeeId")).thenReturn(200L);
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<?> result = controller.createEmployee(requestDTO, bindingResult, httpRequest);

        assertEquals(400, result.getStatusCodeValue());
        assertTrue(result.getBody() instanceof ErrorResponse);
        verify(employeeService, never()).create(any());
    }

    // ================= GET EMPLOYEE =================

    @Test
    void getEmployee_HappyPath_ManagerGetsAnyEmployee() {
        when(httpRequest.getAttribute("role")).thenReturn("MANAGER");
        when(httpRequest.getAttribute("employeeId")).thenReturn(999L);
        when(employeeService.getById(1L)).thenReturn(responseDTO);

        ResponseEntity<EmployeeResponseDTO> result = controller.getEmployee(1L, httpRequest);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("John Doe", result.getBody().getUsername());
        verify(employeeService, times(1)).getById(1L);
    }

    @Test
    void getEmployee_SadPath_EmployeeTriesToAccessOthersData() {
        when(httpRequest.getAttribute("role")).thenReturn("EMPLOYEE");
        when(httpRequest.getAttribute("employeeId")).thenReturn(2L);

        ResponseEntity<EmployeeResponseDTO> result = controller.getEmployee(1L, httpRequest);

        assertEquals(403, result.getStatusCodeValue());
        assertNull(result.getBody());
        verify(employeeService, never()).getById(anyLong());
    }

    // ================= LIST EMPLOYEES =================

    @Test
    void listEmployees_HappyPath_ManagerGetsAllWhenNoManagerIdParam() {
        when(httpRequest.getAttribute("role")).thenReturn("MANAGER");
        when(httpRequest.getAttribute("employeeId")).thenReturn(100L);
        when(employeeService.listAll()).thenReturn(Collections.singletonList(responseDTO));

        ResponseEntity<List<EmployeeResponseDTO>> result = controller.listEmployees(null, httpRequest);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(1, result.getBody().size());
        assertEquals("John Doe", result.getBody().get(0).getUsername());
        verify(employeeService, times(1)).listAll();
        verify(employeeService, never()).listByManager(anyLong());
    }

    @Test
    void listEmployees_HappyPath_ManagerGetsByManagerIdParam() {
        when(httpRequest.getAttribute("role")).thenReturn("MANAGER");
        when(httpRequest.getAttribute("employeeId")).thenReturn(100L);
        when(employeeService.listByManager(100L)).thenReturn(Collections.singletonList(responseDTO));

        ResponseEntity<List<EmployeeResponseDTO>> result = controller.listEmployees(100L, httpRequest);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(1, result.getBody().size());
        assertEquals("John Doe", result.getBody().get(0).getUsername());
        verify(employeeService, times(1)).listByManager(100L);
        verify(employeeService, never()).listAll();
    }

    @Test
    void listEmployees_SadPath_NotManager() {
        when(httpRequest.getAttribute("role")).thenReturn("EMPLOYEE");
        when(httpRequest.getAttribute("employeeId")).thenReturn(50L);

        ResponseEntity<List<EmployeeResponseDTO>> result = controller.listEmployees(null, httpRequest);

        assertEquals(403, result.getStatusCodeValue());
        assertNull(result.getBody());
        verify(employeeService, never()).listByManager(anyLong());
        verify(employeeService, never()).listAll();
    }

    // ================= DELETE EMPLOYEE =================

    @Test
    void deleteEmployee_HappyPath_ManagerDeletesEmployee() {
        when(httpRequest.getAttribute("role")).thenReturn("MANAGER");
        when(httpRequest.getAttribute("employeeId")).thenReturn(100L);

        ResponseEntity<?> result = controller.deleteEmployee(200L, httpRequest);

        verify(employeeService, times(1)).delete(200L);
        assertEquals(204, result.getStatusCodeValue());
    }

    @Test
    void deleteEmployee_SadPath_NotManager() {
        when(httpRequest.getAttribute("role")).thenReturn("EMPLOYEE");
        when(httpRequest.getAttribute("employeeId")).thenReturn(100L);

        ResponseEntity<?> result = controller.deleteEmployee(200L, httpRequest);

        assertEquals(403, result.getStatusCodeValue());
        assertTrue(result.getBody() instanceof ErrorResponse);
        verify(employeeService, never()).delete(anyLong());
    }

    @Test
    void deleteEmployee_SadPath_ManagerTriesToDeleteSelf() {
        when(httpRequest.getAttribute("role")).thenReturn("MANAGER");
        when(httpRequest.getAttribute("employeeId")).thenReturn(100L);

        ResponseEntity<?> result = controller.deleteEmployee(100L, httpRequest);

        assertEquals(400, result.getStatusCodeValue());
        assertTrue(result.getBody() instanceof ErrorResponse);
        verify(employeeService, never()).delete(anyLong());
    }
}
