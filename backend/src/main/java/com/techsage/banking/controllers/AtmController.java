package com.techsage.banking.controllers;

import com.techsage.banking.exceptions.*;
import com.techsage.banking.models.*;
import com.techsage.banking.models.dto.*;
import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.models.dto.responses.*;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.services.*;
import com.techsage.banking.services.interfaces.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.web.bind.annotation.*;

import javax.naming.*;
import javax.naming.AuthenticationException;
import java.util.*;

@RestController
@RequestMapping("/atm")
@Tag(name = "ATM", description = "Endpoints for ATM operations")
public class AtmController extends BaseController {
    private final AtmService atmService;
    private final TurnstileService turnstileService;
    private final BankAccountService bankAccountService;
    private final UserService userService;

    public AtmController(AtmService atmService, TurnstileService turnstileService, BankAccountService bankAccountService, UserService userService) {
        this.atmService = atmService;
        this.turnstileService = turnstileService;
        this.bankAccountService = bankAccountService;
        this.userService = userService;
    }

    @Operation(
            summary = "Login",
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
    public ResponseEntity<BaseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            turnstileService.verifyToken(loginRequest.getCfTurnstileResponse());

            return ResponseEntity.ok().body(atmService.login(loginRequest));
        } catch (TurnstileFailedException e) {
            return ResponseEntity.status(400).body(new MessageDto(400, e.getMessage()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(new MessageDto(401, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageDto(500, e.getMessage()));
        }
    }

    @Operation(
            summary = "Bank Accounts",
            description = "Returns a list of bank accounts for the authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval of bank accounts",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BankAccountDto.class)))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    @GetMapping(value = "/bankAccounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BankAccountDto> bankAccounts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User me = userService.getByEmailRaw(authentication.getName());

        return bankAccountService.findByUserAndType(me, BankAccountType.CHECKING);
    }
}
