package com.techsage.banking.controllers;

import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.BankAccountDto;
import com.techsage.banking.models.dto.BaseDto;
import com.techsage.banking.models.dto.requests.UpdateAbsoluteMinimumBalanceRequestDto;
import com.techsage.banking.models.dto.responses.BankAccountPagedDto;
import com.techsage.banking.models.dto.responses.BankAccountWithoutBalancePagedDto;
import com.techsage.banking.models.dto.responses.MessageDto;
import com.techsage.banking.models.dto.responses.PageResponseDto;
import com.techsage.banking.models.enums.BankAccountType;
import com.techsage.banking.models.info.BankAccountInfoWithoutBalance;
import com.techsage.banking.services.interfaces.BankAccountService;
import com.techsage.banking.services.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BankAccountPagedDto.class)))
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
    public PageResponseDto<BankAccountDto> getBankAccounts(@RequestParam(required = false) BankAccountType type, @ParameterObject Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getByEmailRaw(authentication.getName());

        Page<BankAccountDto> page = bankAccountService.findByUserAndType(user, type, pageable);
        return new PageResponseDto<>(page);
    }

    @Operation(
            summary = "Search bank accounts",
            description = "Searches for bank accounts by first name and last name.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BankAccountWithoutBalancePagedDto.class)))
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
    public PageResponseDto<BankAccountInfoWithoutBalance> findByName(@RequestParam String firstName, @RequestParam String lastName, @ParameterObject Pageable pageable) {
        Page<BankAccountInfoWithoutBalance> page = bankAccountService.findByFirstNameAndLastName(firstName, lastName, pageable);
        return new PageResponseDto<>(page);
    }

    @Operation(
            summary = "Update absolute minimum balance",
            description = "Updates the absolute minimum balance for a specific bank account.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful update",
                            content = @Content(schema = @Schema(implementation = BankAccountDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Bank account not found",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    @PutMapping("/{id}/absoluteLimit")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BaseDto> updateAbsoluteMinimumBalance(@PathVariable long id, @Valid @RequestBody UpdateAbsoluteMinimumBalanceRequestDto updateAbsoluteMinimumBalanceRequestDto) {
        try {
            return ResponseEntity.ok(bankAccountService.updateAbsoluteMinimumBalance(id, updateAbsoluteMinimumBalanceRequestDto.getAbsoluteMinimumBalance()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageDto(400, e.getMessage()));
        }
    }
}
