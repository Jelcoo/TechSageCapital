package com.techsage.banking.services;

import com.techsage.banking.jwt.*;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.UserDto;
import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.models.dto.responses.*;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.repositories.UserRepository;
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


    public UserServiceJpa(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenProvider jwtProvider) {
        this.userRepository = userRepository;
        this.modelMapper = new ModelMapper();
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = (List<User>)userRepository.findAll();
        return users.stream().map(user -> modelMapper.map(user, UserDto.class)).toList();
    }

    @Override
    public User getById(int id) {
        return userRepository.findById(id).get();
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
    public void delete(int id) {
        userRepository.deleteById(id);
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
}
