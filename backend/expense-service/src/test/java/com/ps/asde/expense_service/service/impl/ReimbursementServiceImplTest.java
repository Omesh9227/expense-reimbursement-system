package com.ps.asde.expense_service.service.impl;

import com.ps.asde.expense_service.dto.ReimbursementRequestDTO;
import com.ps.asde.expense_service.dto.ReimbursementResponseDTO;
import com.ps.asde.expense_service.enums.Status;
import com.ps.asde.expense_service.exception.ResourceNotFoundException;
import com.ps.asde.expense_service.model.Reimbursement;
import com.ps.asde.expense_service.repository.ReimbursementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReimbursementServiceImplTest {

    @Mock
    private ReimbursementRepository repository;

    @InjectMocks
    private ReimbursementServiceImpl reimbursementService;

    private Reimbursement reimbursement;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        reimbursement = Reimbursement.builder()
                .id(1L)
                .employeeId(100L)
                .managerId(200L)
                .status(Status.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ---------------- create ----------------
    @Test
    void create_HappyPath() {
        ReimbursementRequestDTO request = ReimbursementRequestDTO.builder()
                .employeeId(100L)
                .managerId(200L)
                .amount(BigDecimal.valueOf(5000.0))
                .description("Travel")
                .build();

        when(repository.save(any(Reimbursement.class))).thenReturn(reimbursement);

        ReimbursementResponseDTO response = reimbursementService.create(request);

        assertNotNull(response);
        assertEquals(100L, response.getEmployeeId());
        verify(repository, times(1)).save(any(Reimbursement.class));
    }

    @Test
    void create_SadPath_SameEmployeeAndManager() {
        ReimbursementRequestDTO request = ReimbursementRequestDTO.builder()
                .employeeId(100L)
                .managerId(100L)
                .build();

        assertThrows(IllegalArgumentException.class, () -> reimbursementService.create(request));
        verify(repository, never()).save(any());
    }

    // ---------------- getById ----------------
    @Test
    void getById_HappyPath() {
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));

        ReimbursementResponseDTO dto = reimbursementService.getById(1L);

        assertEquals(1L, dto.getId());
    }

    @Test
    void getById_SadPath_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reimbursementService.getById(1L));
    }

    // ---------------- listByEmployee ----------------
    @Test
    void listByEmployee_HappyPath() {
        when(repository.findByEmployeeId(100L)).thenReturn(List.of(reimbursement));

        List<ReimbursementResponseDTO> list = reimbursementService.listByEmployee(100L);

        assertEquals(1, list.size());
        assertEquals(100L, list.get(0).getEmployeeId());
    }

    @Test
    void listByEmployee_SadPath_Empty() {
        when(repository.findByEmployeeId(100L)).thenReturn(List.of());

        List<ReimbursementResponseDTO> list = reimbursementService.listByEmployee(100L);

        assertTrue(list.isEmpty());
    }

    // ---------------- listByManager ----------------
    @Test
    void listByManager_HappyPath() {
        when(repository.findByManagerId(200L)).thenReturn(List.of(reimbursement));

        List<ReimbursementResponseDTO> list = reimbursementService.listByManager(200L);

        assertEquals(1, list.size());
        assertEquals(200L, list.get(0).getManagerId());
    }

    @Test
    void listByManager_SadPath_Empty() {
        when(repository.findByManagerId(200L)).thenReturn(List.of());

        List<ReimbursementResponseDTO> list = reimbursementService.listByManager(200L);

        assertTrue(list.isEmpty());
    }

    // ---------------- approve ----------------
    @Test
    void approve_HappyPath() {
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));
        when(repository.save(any(Reimbursement.class))).thenAnswer(inv -> inv.getArgument(0));

        ReimbursementResponseDTO dto = reimbursementService.approve(1L, 200L);

        assertEquals("APPROVED", dto.getStatus());
    }

    @Test
    void approve_SadPath_NotManager() {
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));

        assertThrows(IllegalArgumentException.class, () -> reimbursementService.approve(1L, 999L));
    }

    @Test
    void approve_SadPath_NotPending() {
        reimbursement.setStatus(Status.APPROVED);
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));

        assertThrows(IllegalArgumentException.class, () -> reimbursementService.approve(1L, 200L));
    }

    // ---------------- reject ----------------
    @Test
    void reject_HappyPath() {
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));
        when(repository.save(any(Reimbursement.class))).thenAnswer(inv -> inv.getArgument(0));

        ReimbursementResponseDTO dto = reimbursementService.reject(1L, 200L);

        assertEquals("REJECTED", dto.getStatus());
    }

    @Test
    void reject_SadPath_NotManager() {
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));

        assertThrows(IllegalArgumentException.class, () -> reimbursementService.reject(1L, 999L));
    }

    // ---------------- delete ----------------
    @Test
    void delete_HappyPath_ByEmployeePending() {
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));

        reimbursementService.delete(1L, 100L);

        verify(repository, times(1)).delete(reimbursement);
    }

    @Test
    void delete_SadPath_ByEmployeeNotPending() {
        reimbursement.setStatus(Status.APPROVED);
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));

        assertThrows(IllegalArgumentException.class, () -> reimbursementService.delete(1L, 100L));
        verify(repository, never()).delete(any());
    }

    @Test
    void delete_SadPath_ByManager() {
        when(repository.findById(1L)).thenReturn(Optional.of(reimbursement));

        assertThrows(IllegalArgumentException.class, () -> reimbursementService.delete(1L, 200L));
    }

    @Test
    void delete_SadPath_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reimbursementService.delete(1L, 100L));
    }
}