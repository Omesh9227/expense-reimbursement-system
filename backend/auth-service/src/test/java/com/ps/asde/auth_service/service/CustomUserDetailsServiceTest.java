package com.ps.asde.auth_service.service;

import com.ps.asde.auth_service.model.User;
import com.ps.asde.auth_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository repo;

    @InjectMocks
    private CustomUserDetailsService service;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("admin");
        user.setPasswordHash("secret");
        user.setRole("MANAGER");
    }

    @Test
    void loadUserByUsernameSuccess() {
        when(repo.findByUsername("admin")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("admin");

        assertEquals("admin", details.getUsername());
        assertEquals("secret", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER")));
    }

    @Test
    void loadUserByUsernameNotFound() {
        when(repo.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing"));
    }
}
