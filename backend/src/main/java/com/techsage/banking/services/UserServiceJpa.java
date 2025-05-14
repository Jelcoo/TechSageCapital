package com.techsage.banking.services;

import com.techsage.banking.jwt.*;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.UserDto;
import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.models.dto.responses.*;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.repositories.UserRepository;
import com.techsage.banking.services.interfaces.BankAccountService;
import com.techsage.banking.services.interfaces.UserService;
import org.modelmapper.ModelMapper;
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
    public List<UserDto> getAll() {
        List<User> users = (List<User>)userRepository.findAll();
        return users.stream().map(user -> modelMapper.map(user, UserDto.class)).toList();
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
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public void softDelete(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with ID " + id + " not found"));
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
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
        Optional<User> user = userRepository.getByEmail(loginRequest.getEmail());

        if (user.isEmpty() || !bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
            throw new AuthenticationException("Invalid username/password");
        }

        return this.setUserJwt(user.get());
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

        // Store refresh token in user entity
        user.setRefreshToken(response.getRefreshToken());
        user.setRefreshTokenCreatedAt(LocalDateTime.now());
        userRepository.save(user);

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

    public List<UserDto> findByStatus(UserStatus status) {
        List<User> users = userRepository.findByStatus(status);
        return users.stream().map(user -> modelMapper.map(user, UserDto.class)).toList();
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
        bankAccountService.create(user, BankAccountType.CHECKING, BigDecimal.valueOf(0), BigDecimal.valueOf(0.0));
        bankAccountService.create(user, BankAccountType.SAVINGS, BigDecimal.valueOf(0), BigDecimal.valueOf(0.0));
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Override
    public UserDto updateLimits(long id, UserLimitsRequestDto userLimitsRequestDto) throws IllegalArgumentException {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User with ID " + id + " not found"));
        user.setTransferLimit(userLimitsRequestDto.getTransferLimit());
        user.setDailyLimit(userLimitsRequestDto.getDailyTransferLimit());
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }
}
