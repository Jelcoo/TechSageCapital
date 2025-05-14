package com.techsage.banking.services;

import com.techsage.banking.jwt.*;
import com.techsage.banking.models.*;
import com.techsage.banking.models.dto.*;
import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.models.dto.responses.*;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.repositories.*;
import com.techsage.banking.services.interfaces.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.security.crypto.bcrypt.*;

import javax.naming.*;
import java.math.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceJpaTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtProvider;

    @Mock
    private BankAccountService bankAccountService;

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
        sampleUser.setDailyLimit(BigDecimal.valueOf(100.0));
        sampleUser.setTransferLimit(BigDecimal.valueOf(50.0));
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

    @Test
    void testGetByIdFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        UserDto dto = userService.getById(1L);

        assertEquals(sampleUser.getEmail(), dto.getEmail());
    }

    @Test
    void testGetByIdNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.getById(2L));
    }

    @Test
    void testCreateSuccess() {
        when(userRepository.getByEmail(sampleUser.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("raw-pass")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User created = userService.create(sampleUser);

        assertEquals("encoded", created.getPassword());
        verify(userRepository).save(sampleUser);
    }

    @Test
    void testCreateEmailTaken() {
        when(userRepository.getByEmail(sampleUser.getEmail())).thenReturn(Optional.of(sampleUser));

        assertThrows(IllegalArgumentException.class, () -> userService.create(sampleUser));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdate() {
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);

        User updated = userService.update(sampleUser);
        assertSame(sampleUser, updated);
        verify(userRepository).save(sampleUser);
    }

    @Test
    void testSoftDelete() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.softDelete(1L);

        assertEquals(UserStatus.DELETED, sampleUser.getStatus());
        verify(userRepository).save(sampleUser);
    }

    @Test
    void testReinstateUserToActive() {
        sampleUser.setStatus(UserStatus.DELETED);
        sampleUser.setDailyLimit(BigDecimal.valueOf(10.0));
        sampleUser.setTransferLimit(BigDecimal.valueOf(5.0));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDto dto = userService.reinstateUser(1L);

        assertEquals(UserStatus.ACTIVE, sampleUser.getStatus());
        assertEquals(sampleUser.getEmail(), dto.getEmail());
    }

    @Test
    void testLoginSuccess() throws AuthenticationException {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("raw-pass");

        sampleUser.setPassword("encoded");
        when(userRepository.getByEmail(request.getEmail())).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches(request.getPassword(), "encoded")).thenReturn(true);
        when(jwtProvider.createAccessToken(any(), any())).thenReturn("accToken");
        when(jwtProvider.createRefreshToken(any())).thenReturn("refToken");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        AuthResponseDto resp = userService.login(request);

        assertEquals("accToken", resp.getAccessToken());
        assertEquals("refToken", resp.getRefreshToken());
    }

    @Test
    void testLoginFail() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("wrong");

        when(userRepository.getByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> userService.login(request));
    }

    @Test
    void testRefreshTokenSuccess() throws AuthenticationException {
        RefreshRequestDto req = new RefreshRequestDto();
        req.setRefreshToken("refToken");

        when(jwtProvider.validateToken("refToken")).thenReturn(true);
        when(jwtProvider.getUsernameFromToken("refToken")).thenReturn("test@example.com");
        sampleUser.setRefreshToken("refToken");
        when(userRepository.getByEmail("test@example.com")).thenReturn(Optional.of(sampleUser));
        when(jwtProvider.createAccessToken(any(), any())).thenReturn("newAcc");
        when(jwtProvider.createRefreshToken(any())).thenReturn("newRef");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        AuthResponseDto resp = userService.refreshToken(req);
        assertEquals("newAcc", resp.getAccessToken());
        assertEquals("newRef", resp.getRefreshToken());
    }

    @Test
    void testRefreshTokenInvalid() {
        RefreshRequestDto req = new RefreshRequestDto();
        req.setRefreshToken("bad");
        when(jwtProvider.validateToken("bad")).thenReturn(false);
        assertThrows(AuthenticationException.class, () -> userService.refreshToken(req));
    }

    @Test
    void testRegister() throws AuthenticationException {
        RegisterRequestDto req = new RegisterRequestDto();
        req.setFirstName("First"); req.setLastName("Last");
        req.setEmail("test@example.com"); req.setPassword("pass");
        req.setPhoneNumber("123"); req.setBsn("456");

        when(userRepository.getByEmail(req.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("enc");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtProvider.createAccessToken(any(), any())).thenReturn("a");
        when(jwtProvider.createRefreshToken(any())).thenReturn("r");

        AuthResponseDto resp = userService.register(req);
        assertEquals("a", resp.getAccessToken());
        assertEquals("r", resp.getRefreshToken());
    }

    @Test
    void testGetByEmail() {
        when(userRepository.getByEmail("e")).thenReturn(Optional.of(sampleUser));
        UserDto dto = userService.getByEmail("e");
        assertEquals(sampleUser.getEmail(), dto.getEmail());

        when(userRepository.getByEmail("x")).thenReturn(Optional.empty());
        assertNull(userService.getByEmail("x"));
    }

    @Test
    void testFindByStatus() {
        sampleUser.setStatus(UserStatus.ACTIVE);
        when(userRepository.findByStatus(UserStatus.ACTIVE)).thenReturn(List.of(sampleUser));
        List<UserDto> dtos = userService.findByStatus(UserStatus.ACTIVE);
        assertEquals(1, dtos.size());
    }

    @Test
    void testApproveUser() {
        sampleUser.setStatus(UserStatus.PENDING);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        ApprovalRequestDto req = new ApprovalRequestDto();
        req.setTransferLimit(BigDecimal.valueOf(20.0));
        req.setDailyTransferLimit(BigDecimal.valueOf(100.0));

        UserDto dto = userService.approveUser(1L, req);
        assertEquals(UserStatus.ACTIVE, sampleUser.getStatus());
        verify(bankAccountService, times(2)).create(eq(sampleUser), any(), BigDecimal.valueOf(anyInt()), BigDecimal.valueOf(anyDouble()));
    }

    @Test
    void testUpdateLimits() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserLimitsRequestDto req = new UserLimitsRequestDto();
        req.setTransferLimit(BigDecimal.valueOf(30.0));
        req.setDailyTransferLimit(BigDecimal.valueOf(200.0));

        UserDto dto = userService.updateLimits(1L, req);
        assertEquals(30L, sampleUser.getTransferLimit());
        assertEquals(200L, sampleUser.getDailyLimit());
    }
}
