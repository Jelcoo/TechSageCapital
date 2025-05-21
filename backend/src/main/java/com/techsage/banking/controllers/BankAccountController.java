package com.techsage.banking.controllers;

import com.techsage.banking.models.*;
import com.techsage.banking.models.dto.BankAccountDto;
import com.techsage.banking.models.dto.responses.*;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.models.info.BankAccountInfoWithoutBalance;
import com.techsage.banking.services.interfaces.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/bankAccounts")
@Tag(name = "Bank Accounts", description = "Endpoints for bank account management")
public class BankAccountController extends BaseController {
    private final BankAccountService bankAccountService;
    private final UserService userService;

    public BankAccountController(BankAccountService bankAccountService, UserService userService) {
        this.bankAccountService = bankAccountService;
        this.userService = userService;
    }

    @Operation(
            summary = "All own accounts",
            description = "Returns a list of bank accounts for the authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BankAccountDto.class)))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<BankAccountDto> getBankAccounts(@RequestParam(required = false) BankAccountType type) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getByEmailRaw(authentication.getName());

        return bankAccountService.findByUserAndType(user, type);
    }

    @Operation(
            summary = "Search bank accounts",
            description = "Searches for bank accounts by first name and last name.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BankAccountInfoWithoutBalance.class)))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    @GetMapping("/find")
    @PreAuthorize("hasRole('USER')")
    public List<BankAccountInfoWithoutBalance> findByName(@RequestParam String firstName, @RequestParam String lastName) {
        return bankAccountService.findByFirstNameAndLastName(firstName, lastName);
    }
}
