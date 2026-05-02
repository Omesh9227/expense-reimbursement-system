package com.ps.asde.auth_service.controller;

import com.ps.asde.auth_service.dto.LoginRequest;
import com.ps.asde.auth_service.dto.LoginResponse;
import com.ps.asde.auth_service.dto.RegisterRequest;
import com.ps.asde.auth_service.model.User;
import com.ps.asde.auth_service.repository.UserRepository;
import com.ps.asde.auth_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        var maybe = repo.findByUsername(req.getUsername());
        if (maybe.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        User user = maybe.get();
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getEmployeeId(),
                user.getManagerId()
        );

        return ResponseEntity.ok(LoginResponse.builder().token(token).build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (repo.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if ("EMPLOYEE".equalsIgnoreCase(request.getRole())) {
            if (request.getManagerId() == null) {
                return ResponseEntity.badRequest().body("Employee must be assigned to a manager (managerId required)");
            }
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEmployeeId(request.getEmployeeId());
        user.setManagerId(request.getManagerId());

        repo.save(user);

        return ResponseEntity.ok("User registered successfully");
    }
}
