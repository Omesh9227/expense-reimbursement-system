package com.ps.asde.auth_service.service;

import com.ps.asde.auth_service.model.User;
import com.ps.asde.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Optional<User> findByUsername(String username){ return repo.findByUsername(username); }
    public User saveUser(User u){
        u.setPasswordHash(passwordEncoder.encode(u.getPasswordHash()));
        return repo.save(u);
    }
    public boolean checkPassword(String raw, String encoded){
        return passwordEncoder.matches(raw, encoded);
    }
}
