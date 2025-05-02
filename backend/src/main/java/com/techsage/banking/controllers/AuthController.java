package com.techsage.banking.controllers;

import com.techsage.banking.models.dto.*;
import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.models.dto.responses.*;
import com.techsage.banking.services.interfaces.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<BaseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            return ResponseEntity.ok().body(userService.login(loginRequest));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(new MessageDto(403, e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<BaseDto> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        try {
            return ResponseEntity.ok().body(userService.register(registerRequest));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(new MessageDto(403, e.getMessage()));
        }
    }
}
