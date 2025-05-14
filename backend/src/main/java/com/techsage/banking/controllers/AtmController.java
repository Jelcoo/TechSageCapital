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
import org.iban4j.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
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
    private final UserService userService;

    public AtmController(AtmService atmService, UserService userService) {
        this.atmService = atmService;
        this.userService = userService;
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseDto> deposit(@Valid @RequestBody AtmDepositDto deposit) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getByEmailRaw(authentication.getName());
        try {
            return ResponseEntity.status(200).body(atmService.deposit(deposit, user));
        } catch (TransactionException e) {
            if (e.getReason() == TransactionException.Reason.BANK_ACCOUNT_NOT_FOUND) {
                return ResponseEntity.status(404).body(new MessageDto(404, e.getReason().getMessage()));
            }
            return ResponseEntity.badRequest().body(new MessageDto(400, e.getReason().getMessage()));
        } catch (InvalidCheckDigitException | IbanFormatException e) {
            return ResponseEntity.status(400).body(new MessageDto(400, "Invalid IBAN"));
        }
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseDto> withdraw(@Valid @RequestBody AtmWithdrawDto withdraw) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getByEmailRaw(authentication.getName());
        try {
            return ResponseEntity.status(200).body(atmService.withdraw(withdraw, user));
        } catch (TransactionException e) {
            if (e.getReason() == TransactionException.Reason.BANK_ACCOUNT_NOT_FOUND) {
                return ResponseEntity.status(404).body(new MessageDto(404, e.getReason().getMessage()));
            }
            return ResponseEntity.badRequest().body(new MessageDto(400, e.getReason().getMessage()));
        } catch (InvalidCheckDigitException | IbanFormatException e) {
            return ResponseEntity.status(400).body(new MessageDto(400, "Invalid IBAN"));
        }
    }
}
