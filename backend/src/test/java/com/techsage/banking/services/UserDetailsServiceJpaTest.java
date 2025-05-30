package com.techsage.banking.services;

import com.techsage.banking.models.User;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.core.userdetails.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
class UserDetailsServiceJpaTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceJpa userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_userExists_returnsUserDetails() {
        // Arrange
        String email = "test@example.com";
        String password = "securepassword";
        List<UserRole> roles = List.of(UserRole.ROLE_USER);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setRoles(roles);

        when(userRepository.getByEmail(email)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_userDoesNotExist_throwsException() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.getByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(email)
        );
        assertEquals("User not found with email: " + email, exception.getMessage());
    }
}
