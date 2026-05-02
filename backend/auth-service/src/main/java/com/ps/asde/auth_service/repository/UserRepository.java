package com.ps.asde.auth_service.repository;

import com.ps.asde.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmployeeId(Long employeeId);
}
