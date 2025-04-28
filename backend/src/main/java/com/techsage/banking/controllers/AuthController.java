package com.techsage.banking.controllers;

import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.models.dto.responses.*;
import com.techsage.banking.services.interfaces.*;
import org.springframework.web.bind.annotation.*;

import javax.naming.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequest) throws AuthenticationException {
        return userService.login(loginRequest);
    }
}
