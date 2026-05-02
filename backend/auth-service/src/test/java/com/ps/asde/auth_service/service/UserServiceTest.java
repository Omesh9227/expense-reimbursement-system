package com.ps.asde.auth_service.service;

import com.ps.asde.auth_service.model.User;
import com.ps.asde.auth_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository repo;

    @InjectMocks
    private UserService userService;

    private BCryptPasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        encoder = new BCryptPasswordEncoder();
    }

    @Test
    void testFindByUsername_HappyPath() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setPasswordHash("hashed");

        when(repo.findByUsername("john")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("john");

        assertTrue(result.isPresent());
        assertEquals("john", result.get().getUsername());
        verify(repo, times(1)).findByUsername("john");
    }

    @Test
    void testFindByUsername_SadPath_NotFound() {
        when(repo.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("unknown");

        assertFalse(result.isPresent());
        verify(repo, times(1)).findByUsername("unknown");
    }

    @Test
    void testSaveUser_HappyPath() {
        User user = new User();
        user.setUsername("john");
        user.setPasswordHash("plainPassword");

        when(repo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.saveUser(user);

        assertNotNull(saved.getPasswordHash());
        assertNotEquals("plainPassword", saved.getPasswordHash());
        assertTrue(encoder.matches("plainPassword", saved.getPasswordHash()));
        verify(repo, times(1)).save(any(User.class));
    }

    @Test
    void testCheckPassword_HappyPath_Match() {
        String raw = "mypassword";
        String encoded = encoder.encode(raw);

        boolean result = userService.checkPassword(raw, encoded);

        assertTrue(result);
    }

    @Test
    void testCheckPassword_SadPath_Mismatch() {
        String raw = "mypassword";
        String encoded = encoder.encode("different");

        boolean result = userService.checkPassword(raw, encoded);

        assertFalse(result);
    }
}
