package com.techsage.banking.controllers;

import com.techsage.banking.exceptions.*;
import com.techsage.banking.models.dto.*;
import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.models.dto.responses.*;
import com.techsage.banking.services.*;
import com.techsage.banking.services.interfaces.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.naming.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController extends BaseController {
    private final UserService userService;
    private final TurnstileService turnstileService;

    public AuthController(UserService userService, TurnstileService turnstileService) {
        this.userService = userService;
        this.turnstileService = turnstileService;
    }

    @Operation(
            summary = "Login a user",
            description = "Authenticates a user and returns a token if successful.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful login",
                            content = @Content(schema = @Schema(implementation = AuthResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Turnstile verification failed",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid credentials",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) throws AuthenticationException {
        turnstileService.verifyToken(loginRequest.getCfTurnstileResponse());

        return ResponseEntity.ok().body(userService.login(loginRequest));
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User registered successfully",
                            content = @Content(schema = @Schema(implementation = AuthResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Registration failed (e.g., duplicate email) or turnstile verification failed",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseDto> register(@Valid @RequestBody RegisterRequestDto registerRequest) throws AuthenticationException {
        turnstileService.verifyToken(registerRequest.getCfTurnstileResponse());

        return ResponseEntity.ok().body(userService.register(registerRequest));
    }

    @Operation(
            summary = "Refresh token",
            description = "Refreshes the access token using a refresh token.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token refreshed successfully",
                            content = @Content(schema = @Schema(implementation = AuthResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid refresh token",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseDto> refreshToken(@RequestBody RefreshRequestDto refreshRequest) throws AuthenticationException {
        return ResponseEntity.ok().body(userService.refreshToken(refreshRequest));
    }
}
