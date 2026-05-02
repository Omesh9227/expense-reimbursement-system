package com.ps.asde.expense_service.controller;

import com.ps.asde.expense_service.dto.ReimbursementRequestDTO;
import com.ps.asde.expense_service.dto.ReimbursementResponseDTO;
import com.ps.asde.expense_service.service.ReimbursementService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExpenseControllerTest {

    @InjectMocks
    private ExpenseController controller;

    @Mock
    private ReimbursementService service;

    @Mock
    private HttpServletRequest request;

    @Mock
    private BindingResult bindingResult;

    private ReimbursementResponseDTO sample;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        sample = new ReimbursementResponseDTO();
        sample.setId(1L);
        sample.setEmployeeId(101L);
        sample.setManagerId(201L);
        sample.setAmount(BigDecimal.valueOf(500));
        sample.setDescription("Travel");
        sample.setStatus("PENDING");
    }

    // ------------------ CREATE ------------------

    @Test
    void testCreate_Success() {
        ReimbursementRequestDTO req = new ReimbursementRequestDTO();
        req.setAmount(BigDecimal.valueOf(100));
        req.setDescription("Taxi");
        req.setEmployeeId(101L);
        req.setManagerId(201L);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(service.create(any())).thenReturn(sample);

        ResponseEntity<?> response = controller.create(req, bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ReimbursementResponseDTO);
    }

    @Test
    void testCreate_WithValidationErrors() {
        ReimbursementRequestDTO req = new ReimbursementRequestDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<?> response = controller.create(req, bindingResult);

        assertEquals(400, response.getStatusCodeValue());
    }

    // ------------------ GET BY ID ------------------

    @Test
    void testGetById() {
        when(service.getById(1L)).thenReturn(sample);

        ResponseEntity<ReimbursementResponseDTO> response = controller.getById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(101L, response.getBody().getEmployeeId());
    }

    // ------------------ LIST ------------------

    @Test
    void testList_ByEmployee() {
        when(service.listByEmployee(101L)).thenReturn(Collections.singletonList(sample));

        ResponseEntity<List<ReimbursementResponseDTO>> response = controller.list(101L, null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testList_ByManager() {
        when(service.listByManager(201L)).thenReturn(Arrays.asList(sample));

        ResponseEntity<List<ReimbursementResponseDTO>> response = controller.list(null, 201L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testList_All() {
        when(service.listAll()).thenReturn(Arrays.asList(sample));

        ResponseEntity<List<ReimbursementResponseDTO>> response = controller.list(null, null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    // ------------------ APPROVE ------------------

    @Test
    void testApprove_Success() {
        when(request.getAttribute("role")).thenReturn("MANAGER");
        when(request.getAttribute("managerId")).thenReturn(201L);
        when(service.getById(1L)).thenReturn(sample);
        when(service.approve(1L, 201L)).thenReturn(sample);

        ResponseEntity<ReimbursementResponseDTO> response = controller.approve(1L, request);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testApprove_AsEmployee_Forbidden() {
        when(request.getAttribute("role")).thenReturn("EMPLOYEE");

        ResponseEntity<ReimbursementResponseDTO> response = controller.approve(1L, request);

        assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    void testApprove_ManagerNotOwner_Forbidden() {
        sample.setManagerId(999L);

        when(request.getAttribute("role")).thenReturn("MANAGER");
        when(request.getAttribute("managerId")).thenReturn(201L);
        when(service.getById(1L)).thenReturn(sample);

        ResponseEntity<ReimbursementResponseDTO> response = controller.approve(1L, request);

        assertEquals(403, response.getStatusCodeValue());
    }

    // ------------------ REJECT ------------------

    @Test
    void testReject_Success() {
        when(request.getAttribute("role")).thenReturn("MANAGER");
        when(request.getAttribute("managerId")).thenReturn(201L);
        when(service.getById(1L)).thenReturn(sample);
        when(service.reject(1L, 201L)).thenReturn(sample);

        ResponseEntity<ReimbursementResponseDTO> response = controller.reject(1L, request);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testReject_AsEmployee_Forbidden() {
        when(request.getAttribute("role")).thenReturn("EMPLOYEE");

        ResponseEntity<ReimbursementResponseDTO> response = controller.reject(1L, request);

        assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    void testReject_ManagerNotOwner_Forbidden() {
        sample.setManagerId(999L);

        when(request.getAttribute("role")).thenReturn("MANAGER");
        when(request.getAttribute("managerId")).thenReturn(201L);
        when(service.getById(1L)).thenReturn(sample);

        ResponseEntity<ReimbursementResponseDTO> response = controller.reject(1L, request);

        assertEquals(403, response.getStatusCodeValue());
    }

    // ------------------ DELETE ------------------

    @Test
    void testDelete_Success() {
        ResponseEntity<Void> response = controller.delete(1L, 101L);

        assertEquals(204, response.getStatusCodeValue());
        verify(service, times(1)).delete(1L, 101L);
    }
}
