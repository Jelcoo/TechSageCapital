package com.techsage.banking.services;

import com.techsage.banking.jwt.*;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.UserDto;
import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.models.dto.responses.*;
import com.techsage.banking.models.dto.requests.UpdateUserRequestDto;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.repositories.UserRepository;
import com.techsage.banking.services.interfaces.BankAccountService;
import com.techsage.banking.services.interfaces.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.stereotype.Service;

import javax.naming.*;
import java.math.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceJpa implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtProvider;
    private final BankAccountService bankAccountService;

    public UserServiceJpa(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenProvider jwtProvider, BankAccountService bankAccountService) {
        this.userRepository = userRepository;
        this.modelMapper = new ModelMapper();
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtProvider = jwtProvider;
        this.bankAccountService = bankAccountService;
    }

    @Override
    public UserDto getById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User with ID " + id + " not found"));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public User create(User user) {
        if (userRepository.getByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already taken");
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public UserDto update(long id, UpdateUserRequestDto user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with ID " + id + " not found"));
        User convertedUser = modelMapper.map(user, User.class);
        convertedUser.setId(id);
        convertedUser.setPassword(existingUser.getPassword());
        return modelMapper.map(userRepository.save(convertedUser), UserDto.class);
    }

    @Override
    public UserDto updateSelf(String currentEmail , UpdateSelfRequestDto user) {
        User existingUser = getByEmailRaw(currentEmail);
        if (!existingUser.getEmail().equals(currentEmail)) {
            throw new IllegalArgumentException("Email is already taken");
        }
        existingUser.setEmail(user.getEmail());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        return modelMapper.map(userRepository.save(existingUser), UserDto.class);
    }

    @Override
    public UserDto softDelete(long id, User authenticatedUser) {
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with ID " + id + " not found."));

        if (authenticatedUser.getEmail().equals(targetUser.getEmail())) {
            throw new IllegalArgumentException("You cannot delete your own account.");
        }

        if (targetUser.getStatus() == UserStatus.DELETED) {
            throw new IllegalArgumentException("User is already marked as deleted.");
        }

        boolean isEmployee = targetUser.getRoles().contains(UserRole.ROLE_EMPLOYEE);
        boolean isAdmin = targetUser.getRoles().contains(UserRole.ROLE_ADMIN);
        boolean isAuthenticatedAdmin = authenticatedUser.getRoles().contains(UserRole.ROLE_ADMIN);

        if (isEmployee && !isAuthenticatedAdmin) {
            throw new IllegalArgumentException("You are not authorized to delete an employee account.");
        }

        if (isAdmin && !isAuthenticatedAdmin) {
            throw new IllegalArgumentException("You are not authorized to delete an admin account.");
        }

        targetUser.setStatus(UserStatus.DELETED);
        userRepository.save(targetUser);

        return modelMapper.map(targetUser, UserDto.class);
    }


    @Override
    public UserDto reinstateUser(long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with ID " + id + " not found"));
        user.setStatus(UserStatus.ACTIVE);

        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequest) throws AuthenticationException {
        User user = userRepository.getByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid username/password"));

        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid username/password");
        }

        if (user.getStatus() == UserStatus.DELETED) {
            throw new AuthenticationException("Account is not accessible");
        }

        if (loginRequest.getAuthenticationScope().equals(AuthenticationScope.ATM) && !user.getRoles().contains(UserRole.ROLE_CUSTOMER)) {
            throw new AuthenticationException("You cannot log into the ATM");
        }

        if (loginRequest.getAuthenticationScope().equals(AuthenticationScope.ATM) && user.getBankAccounts().isEmpty()) {
            throw new AuthenticationException("You don't have any bank accounts, so the ATM is not accessible");
        }

        if (loginRequest.getAuthenticationScope().equals(AuthenticationScope.ATM)) {
            return this.setUserAtmJwt(user);
        } else {
            return this.setUserJwt(user);
        }
    }

    @Override
    public AuthResponseDto refreshToken(RefreshRequestDto refreshRequest) throws AuthenticationException {
        String refreshToken = refreshRequest.getRefreshToken();
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new AuthenticationException("Invalid refresh token");
        }

        String username = jwtProvider.getUsernameFromToken(refreshToken);
        Optional<User> user = userRepository.getByEmail(username);

        if (user.isEmpty() || !refreshToken.equals(user.get().getRefreshToken())) {
            throw new AuthenticationException("Invalid refresh token");
        }

        return this.setUserJwt(user.get());
    }

    @Override
    public AuthResponseDto register(RegisterRequestDto registerRequest) throws AuthenticationException {
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setBsn(registerRequest.getBsn());
        user.setPassword(registerRequest.getPassword());

        User createdUser = this.create(user);

        return this.setUserJwt(createdUser);
    }

    private AuthResponseDto setUserJwt(User user) {
        AuthResponseDto response = new AuthResponseDto();
        response.setAccessToken(jwtProvider.createAccessToken(user.getEmail(), user.getRoles()));
        response.setRefreshToken(jwtProvider.createRefreshToken(user.getEmail()));
        response.setScope(AuthenticationScope.BANK);

        // Store refresh token in user entity
        user.setRefreshToken(response.getRefreshToken());
        user.setRefreshTokenCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        return response;
    }

    private AuthResponseDto setUserAtmJwt(User user) {
        AuthResponseDto response = new AuthResponseDto();
        response.setAccessToken(jwtProvider.createAtmToken(user.getEmail(), user.getRoles()));
        response.setScope(AuthenticationScope.ATM);

        return response;
    }

    @Override
    public UserDto getByEmail(String email) {
        return userRepository.getByEmail(email).map(user -> modelMapper.map(user, UserDto.class)).orElse(null);
    }

    @Override
    public User getByEmailRaw(String email) {
        return userRepository.getByEmail(email).orElse(null);
    }

    public Page<UserDto> findByStatus(UserStatus status, Pageable pageable) {
        Page<User> usersPage = userRepository.findByStatus(status, pageable);
        return usersPage.map(user -> modelMapper.map(user, UserDto.class));
    }

    @Override
    public UserDto approveUser(long id, ApprovalRequestDto approvalRequestDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User with ID " + id + " not found"));
        if (user.getStatus() != UserStatus.PENDING) {
            throw new IllegalArgumentException("User is already approved");
        }
        user.setStatus(UserStatus.ACTIVE);
        user.setTransferLimit(approvalRequestDto.getTransferLimit());
        user.setDailyLimit(approvalRequestDto.getDailyTransferLimit());
        bankAccountService.create(user, BankAccountType.CHECKING, approvalRequestDto.getAbsoluteLimitChecking(), BigDecimal.valueOf(0.0));
        bankAccountService.create(user, BankAccountType.SAVINGS, approvalRequestDto.getAbsoluteLimitSavings(), BigDecimal.valueOf(0.0));
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Override
    public UserDto updatePassword(long id, UpdateUserPasswordRequestDto updateUserPasswordRequestDto) throws IllegalArgumentException {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User with ID " + id + " not found"));
        user.setPassword(bCryptPasswordEncoder.encode(updateUserPasswordRequestDto.getNewPassword()));
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Override
    public AuthResponseDto updateOwnPassword(String email, UpdatePasswordRequestDto updatePasswordRequestDto) throws IllegalArgumentException {
        User user = userRepository.getByEmail(email).orElseThrow(() -> new NoSuchElementException("User with email " + email + " not found"));
        if (!bCryptPasswordEncoder.matches(updatePasswordRequestDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPassword(bCryptPasswordEncoder.encode(updatePasswordRequestDto.getNewPassword()));
        return this.setUserJwt(user);
    }

    @Override
    public UserDto updateRole(long id, UserRole role) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User with ID " + id + " not found"));
        List<UserRole> roles = new ArrayList<>(user.getRoles());
        roles.remove(UserRole.ROLE_EMPLOYEE);
        roles.remove(UserRole.ROLE_CUSTOMER);
        roles.add(role);
        user.setRoles(roles);
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }
}
