package com.techsage.banking.services;

import com.techsage.banking.jwt.JwtTokenProvider;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.UserDto;
import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.models.dto.responses.AuthResponseDto;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.repositories.UserRepository;
import com.techsage.banking.services.interfaces.BankAccountService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
class UserServiceJpaTest {

    @Mock private UserRepository userRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private BankAccountService bankAccountService;

    @InjectMocks private UserServiceJpa userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getById_userExists_returnsUserDto() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto userDto = userService.getById(1L);
        assertEquals("test@example.com", userDto.getEmail());
    }

    @Test
    void create_newUser_savesUser() {
        User user = new User();
        user.setEmail("new@example.com");
        user.setPassword("rawPassword");

        when(userRepository.getByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.create(user);
        assertEquals("encodedPassword", savedUser.getPassword());
    }

    @Test
    void update_existingUser_updatesData() {
        User existing = new User();
        existing.setId(1L);
        existing.setPassword("oldPassword");

        UpdateUserRequestDto dto = new UpdateUserRequestDto();
        dto.setEmail("updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userService.update(1L, dto);
        assertEquals("updated@example.com", result.getEmail());
    }

    @Test
    void updateSelf_validRequest_updatesEmailAndPhone() {
        User existing = new User();
        existing.setEmail("current@example.com");

        UpdateSelfRequestDto requestDto = new UpdateSelfRequestDto();
        requestDto.setEmail("new@example.com");
        requestDto.setPhoneNumber("123456789");

        when(userRepository.getByEmail("current@example.com")).thenReturn(Optional.of(existing));
        when(userRepository.save(any())).thenReturn(existing);

        UserDto result = userService.updateSelf("current@example.com", requestDto);
        assertEquals("new@example.com", result.getEmail());
        assertEquals("123456789", result.getPhoneNumber());
    }

    @Test
    void softDelete_existingUser_setsDeletedStatus() {
        User user = new User();
        user.setId(1L);
        user.setStatus(UserStatus.ACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDto result = userService.softDelete(1L);
        assertEquals(UserStatus.DELETED, result.getStatus());
    }

    @Test
    void reinstateUser_deletedUser_setsStatusActive() {
        User user = new User();
        user.setId(1L);
        user.setStatus(UserStatus.DELETED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDto result = userService.reinstateUser(1L);
        assertEquals(UserStatus.ACTIVE, result.getStatus());
    }

    @Test
    void login_validCredentials_returnsAuthResponse() throws Exception {
        User user = new User();
        user.setEmail("login@example.com");
        user.setPassword("encodedPassword");
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(List.of(UserRole.ROLE_USER));

        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("login@example.com");
        loginRequest.setPassword("plainPassword");
        loginRequest.setAuthenticationScope(AuthenticationScope.BANK.toString());

        when(userRepository.getByEmail("login@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.createAccessToken(any(), any())).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken(any())).thenReturn("refresh-token");
        when(userRepository.save(any(User.class))).thenReturn(user);

        AuthResponseDto response = userService.login(loginRequest);
        assertEquals("access-token", response.getAccessToken());
        assertEquals(AuthenticationScope.BANK, response.getScope());
    }

    @Test
    void login_invalidPassword_throwsAuthException() {
        User user = new User();
        user.setEmail("login@example.com");
        user.setPassword("encodedPassword");
        user.setStatus(UserStatus.ACTIVE);

        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("login@example.com");
        loginRequest.setPassword("wrongPassword");

        when(userRepository.getByEmail("login@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> userService.login(loginRequest));
    }

    @Test
    void refreshToken_validToken_returnsNewJwt() throws Exception {
        User user = new User();
        user.setEmail("refresh@example.com");
        user.setRefreshToken("valid-refresh-token");

        RefreshRequestDto requestDto = new RefreshRequestDto();
        requestDto.setRefreshToken("valid-refresh-token");

        when(jwtTokenProvider.validateToken("valid-refresh-token")).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken("valid-refresh-token")).thenReturn("refresh@example.com");
        when(userRepository.getByEmail("refresh@example.com")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.createAccessToken(any(), any())).thenReturn("new-access");
        when(jwtTokenProvider.createRefreshToken(any())).thenReturn("new-refresh");
        when(userRepository.save(any())).thenReturn(user);

        AuthResponseDto response = userService.refreshToken(requestDto);
        assertEquals("new-access", response.getAccessToken());
    }

    @Test
    void register_validRequest_createsUserAndReturnsJwt() throws Exception {
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("register@example.com");
        dto.setPassword("1234");

        when(userRepository.getByEmail("register@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtTokenProvider.createAccessToken(any(), any())).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken(any())).thenReturn("refresh-token");

        AuthResponseDto response = userService.register(dto);
        assertNotNull(response.getAccessToken());
    }

    @Test
    void getByEmail_existingUser_returnsDto() {
        User user = new User();
        user.setEmail("user@example.com");

        when(userRepository.getByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserDto dto = userService.getByEmail("user@example.com");
        assertEquals("user@example.com", dto.getEmail());
    }

    @Test
    void getByEmailRaw_existingUser_returnsEntity() {
        User user = new User();
        user.setEmail("user@example.com");

        when(userRepository.getByEmail("user@example.com")).thenReturn(Optional.of(user));

        User result = userService.getByEmailRaw("user@example.com");
        assertEquals("user@example.com", result.getEmail());
    }

    @Test
    void findByStatus_returnsPageOfUserDto() {
        User user = new User();
        user.setEmail("status@example.com");

        Page<User> userPage = new PageImpl<>(List.of(user));
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findByStatus(UserStatus.ACTIVE, pageable)).thenReturn(userPage);

        Page<UserDto> result = userService.findByStatus(UserStatus.ACTIVE, pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals("status@example.com", result.getContent().get(0).getEmail());
    }

    @Test
    void approveUser_pendingUser_activatesAndCreatesAccounts() {
        User user = new User();
        user.setId(1L);
        user.setStatus(UserStatus.PENDING);

        ApprovalRequestDto approvalDto = new ApprovalRequestDto();
        approvalDto.setTransferLimit(BigDecimal.valueOf(1000));
        approvalDto.setDailyTransferLimit(BigDecimal.valueOf(5000));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDto result = userService.approveUser(1L, approvalDto);

        assertEquals(UserStatus.ACTIVE, result.getStatus());
        verify(bankAccountService, times(2)).create(eq(user), any(), any(), any());
    }
}
