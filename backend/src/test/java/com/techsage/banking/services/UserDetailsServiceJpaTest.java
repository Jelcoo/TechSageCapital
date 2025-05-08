package com.techsage.banking.services;

import com.techsage.banking.repositories.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.security.core.userdetails.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceJpaTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceJpa userDetailsService;

    private com.techsage.banking.models.User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new com.techsage.banking.models.User();
        sampleUser.setId(42L);
        sampleUser.setEmail("user@example.com");
        sampleUser.setPassword("secret");
        sampleUser.setRoles(List.of());
    }

    @Test
    void loadUserByUsername_whenUserExists_returnsUserDetails() {
        when(userRepository.getByEmail("user@example.com"))
                .thenReturn(Optional.of(sampleUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("user@example.com");

        assertEquals(sampleUser.getEmail(), userDetails.getUsername());
        assertEquals(sampleUser.getPassword(), userDetails.getPassword());

        assertTrue(userDetails.getAuthorities().isEmpty(), "Expected no authorities for a user with no roles");
    }

    @Test
    void loadUserByUsername_whenUserNotFound_throwsException() {
        when(userRepository.getByEmail("missing@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("missing@example.com")
        );

        assertTrue(ex.getMessage().contains("User not found with email: missing@example.com"));
        verify(userRepository).getByEmail("missing@example.com");
    }
}