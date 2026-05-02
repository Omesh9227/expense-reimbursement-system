package com.ps.asde.employee_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(nullable = false)
    private Long employeeId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Role {
        EMPLOYEE,
        MANAGER;

        public static Role fromString(String s) {
            if (s == null) return null;
            switch (s.trim().toUpperCase()) {
                case "EMPLOYEE": return EMPLOYEE;
                case "MANAGER": return MANAGER;
                default: throw new IllegalArgumentException("Invalid role: " + s);
            }
        }
    }
}
