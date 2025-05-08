package com.techsage.banking.services;

import com.techsage.banking.jwt.*;
import com.techsage.banking.models.*;
import com.techsage.banking.models.dto.*;
import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.models.dto.responses.*;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.repositories.*;
import com.techsage.banking.services.interfaces.*;
import org.modelmapper.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.stereotype.*;

import javax.naming.*;
import java.time.*;
import java.util.*;

@Service
public class AtmServiceJpa implements AtmService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtProvider;

    public AtmServiceJpa(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenProvider jwtProvider) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public AtmAuthResponseDto login(LoginRequestDto loginRequest) throws AuthenticationException {
        Optional<User> user = userRepository.getByEmail(loginRequest.getEmail());

        if (user.isEmpty() || !bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
            throw new AuthenticationException("Invalid username/password");
        }

        AtmAuthResponseDto response = new AtmAuthResponseDto();
        response.setAtmToken(jwtProvider.createAtmToken(user.get().getEmail()));

        return response;
    }
}
