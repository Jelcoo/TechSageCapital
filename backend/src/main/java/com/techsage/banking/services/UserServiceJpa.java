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
        User user = userRepository.findById(id).get();
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequest) throws AuthenticationException {
        Optional<User> user = userRepository.getByEmail(loginRequest.getEmail());

        if (user.isEmpty() || !bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
            throw new AuthenticationException("Invalid username/password");
        }

        LoginResponseDto response = new LoginResponseDto();
        response.setToken(jwtProvider.createToken(user.get().getEmail(), user.get().getRoles()));

        return response;
    }

    @Override
    public RegisterResponseDto register(RegisterRequestDto registerRequest) throws AuthenticationException {
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setBsn(registerRequest.getBsn());
        user.setPassword(registerRequest.getPassword());

        User createdUser = this.create(user);
        RegisterResponseDto response = new RegisterResponseDto();
        response.setToken(jwtProvider.createToken(createdUser.getEmail(), createdUser.getRoles()));

        return response;
    }

    @Override
    public UserDto getByEmail(String email) {
        return userRepository.getByEmail(email).map(user -> modelMapper.map(user, UserDto.class)).orElse(null);
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
        bankAccountService.create(user, BankAccountType.CHECKING, approvalRequestDto.getAbsoluteMinimumBalance(), 0.0);
        bankAccountService.create(user, BankAccountType.SAVINGS, 0, 0.0);
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
