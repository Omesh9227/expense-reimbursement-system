package com.ps.asde.auth_service.controller;

import com.ps.asde.auth_service.dto.LoginRequest;
import com.ps.asde.auth_service.dto.LoginResponse;
import com.ps.asde.auth_service.dto.RegisterRequest;
import com.ps.asde.auth_service.model.User;
import com.ps.asde.auth_service.repository.UserRepository;
import com.ps.asde.auth_service.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private UserRepository repo;
    private JwtUtil jwtUtil;
    private AuthController controller;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        repo = mock(UserRepository.class);
        jwtUtil = mock(JwtUtil.class);
        controller = new AuthController(repo, jwtUtil);
    }

    @Test
    void testLoginSuccess() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setPasswordHash(passwordEncoder.encode("secret"));
        user.setRole("EMPLOYEE");
        user.setEmployeeId(1001L);
        user.setManagerId(2001L);

        when(repo.findByUsername("john")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(any(), any(), any(), any(), any())).thenReturn("mock-token");

        LoginRequest request = new LoginRequest("john", "secret");
        ResponseEntity<?> response = controller.login(request);

        assertEquals(200, response.getStatusCodeValue());
        LoginResponse body = (LoginResponse) response.getBody();
        assertNotNull(body);
        assertEquals("mock-token", body.getToken());
    }

    @Test
    void testLoginInvalidUsername() {
        when(repo.findByUsername("wrong")).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("wrong", "secret");
        ResponseEntity<?> response = controller.login(request);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    void testLoginInvalidPassword() {
        User user = new User();
        user.setUsername("john");
        user.setPasswordHash(passwordEncoder.encode("correct"));

        when(repo.findByUsername("john")).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest("john", "wrong");
        ResponseEntity<?> response = controller.login(request);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    void testLoginEmptyPassword() {
        User user = new User();
        user.setUsername("john");
        user.setPasswordHash(passwordEncoder.encode("somePassword"));

        when(repo.findByUsername("john")).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest("john", "");
        ResponseEntity<?> response = controller.login(request);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    void testLoginNullUsername() {
        LoginRequest request = new LoginRequest(null, "somePass");
        ResponseEntity<?> response = controller.login(request);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newUser");
        request.setPassword("pass123");
        request.setRole("MANAGER");
        request.setEmployeeId(3001L);
        request.setManagerId(null);

        when(repo.findByUsername("newUser")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.register(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody());
        verify(repo, times(1)).save(ArgumentMatchers.any(User.class));
    }

    @Test
    void testRegisterDuplicateUsername() {
        User existing = new User();
        existing.setUsername("existingUser");

        RegisterRequest request = new RegisterRequest();
        request.setUsername("existingUser");
        request.setPassword("pass");
        request.setRole("EMPLOYEE");

        when(repo.findByUsername("existingUser")).thenReturn(Optional.of(existing));

        ResponseEntity<?> response = controller.register(request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Username already exists", response.getBody());
        verify(repo, never()).save(any(User.class));
    }

    @Test
    void testRegisterEmployeeWithoutManager() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("empUser");
        request.setPassword("pass");
        request.setRole("EMPLOYEE");
        request.setEmployeeId(4001L);
        request.setManagerId(null);

        when(repo.findByUsername("empUser")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.register(request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Employee must be assigned to a manager (managerId required)", response.getBody());
        verify(repo, never()).save(any(User.class));
    }

    @Test
    void testRegisterNullUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(null);
        request.setPassword("pass");
        request.setRole("MANAGER");

        when(repo.findByUsername(null)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.register(request);

        assertEquals(200, response.getStatusCodeValue());
        verify(repo, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterEmptyRole() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user123");
        request.setPassword("pass");
        request.setRole("");

        when(repo.findByUsername("user123")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.register(request);

        assertEquals(200, response.getStatusCodeValue());
        verify(repo, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterNullRole() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user123");
        request.setPassword("pass");
        request.setRole(null);

        when(repo.findByUsername("user123")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.register(request);

        assertEquals(200, response.getStatusCodeValue());
        verify(repo, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterManagerWithEmployeeIdNull() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("managerUser");
        request.setPassword("pass");
        request.setRole("MANAGER");
        request.setEmployeeId(null);
        request.setManagerId(null);

        when(repo.findByUsername("managerUser")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.register(request);

        assertEquals(200, response.getStatusCodeValue());
        verify(repo, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterEmployeeWithManager() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("empWithManager");
        request.setPassword("empPass");
        request.setRole("EMPLOYEE");
        request.setEmployeeId(5001L);
        request.setManagerId(6001L);

        when(repo.findByUsername("empWithManager")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.register(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody());
        verify(repo, times(1)).save(any(User.class));
    }

    @Test
    void testLoginRepoThrowsException() {
        when(repo.findByUsername("john")).thenThrow(new RuntimeException("DB error"));

        LoginRequest request = new LoginRequest("john", "pass");
        assertThrows(RuntimeException.class, () -> controller.login(request));
    }

    @Test
    void testRegisterRepoThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user");
        request.setPassword("pass");
        request.setRole("EMPLOYEE");
        request.setEmployeeId(1001L);
        request.setManagerId(2001L);

        when(repo.findByUsername("user")).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> controller.register(request));
    }
}
