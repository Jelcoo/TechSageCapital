package com.techsage.banking.services;

import com.techsage.banking.models.*;
import com.techsage.banking.models.dto.*;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.repositories.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceJpaTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceJpa userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setEmail("test@example.com");
        sampleUser.setPassword("raw-pass");
        sampleUser.setStatus(UserStatus.ACTIVE);
        sampleUser.setDailyLimit(100.0);
        sampleUser.setTransferLimit(50.0);
    }

    @Test
    void testGetAll() {
        List<User> users = List.of(sampleUser);
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> result = userService.getAll();

        assertEquals(1, result.size());
        assertEquals(sampleUser.getEmail(), result.get(0).getEmail());
        verify(userRepository).findAll();
    }
}