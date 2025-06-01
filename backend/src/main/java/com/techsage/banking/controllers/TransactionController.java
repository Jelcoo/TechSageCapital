package com.techsage.banking.controllers;


import com.techsage.banking.exceptions.TransactionException;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.*;
import com.techsage.banking.models.dto.requests.AllTransactionFilterRequestDto;
import com.techsage.banking.models.dto.requests.TransactionFilterRequestDto;
import com.techsage.banking.models.dto.requests.TransactionRequestDto;
import com.techsage.banking.models.dto.responses.*;
import com.techsage.banking.services.interfaces.TransactionService;
import com.techsage.banking.services.interfaces.UserService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.*;
import org.iban4j.IbanFormatException;
import org.iban4j.InvalidCheckDigitException;
import org.springdoc.core.annotations.*;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transactions", description = "Endpoints for transaction management")
public class TransactionController extends BaseController {
    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }
    
    @Operation(
            summary = "Get transaction by ID",
            description = "Returns a transaction by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval",
                            content = @Content(schema = @Schema(implementation = TransactionPagedDto.class))
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
                            description = "Transaction not found",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    @GetMapping("/{bankAccountId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public PageResponseDto<TransactionDto> getTransactionsForId(@PathVariable long bankAccountId, @ParameterObject Pageable pageable, TransactionFilterRequestDto filter) {
        Page<TransactionDto> page = transactionService.getByAccountId(bankAccountId, pageable, filter);
        return new PageResponseDto<>(page);
    }

    @Operation(
            summary = "Get own transactions",
            description = "Returns a list of transactions for the authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TransactionPagedDto.class)))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    @GetMapping("/{bankAccountId}/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public PageResponseDto<TransactionDto> getCustomerTransactionsForId(@PathVariable long bankAccountId, @ParameterObject Pageable pageable, TransactionFilterRequestDto filter) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Page<TransactionDto> page = transactionService.getByAccountIdAndCustomer(bankAccountId, authentication.getName(), pageable, filter);
        return new PageResponseDto<>(page);
    }

    @Operation(
            summary = "Get all transactions",
            description = "Returns a list of all transactions.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TransactionPagedDto.class)))
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
                    )
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public PageResponseDto<TransactionDto> getAllTransactions(@ParameterObject Pageable pageable, AllTransactionFilterRequestDto filter) {
        Page<TransactionDto> page = transactionService.getAll(pageable, filter);
        return new PageResponseDto<>(page);
    }

    @Operation(
            summary = "Create a transaction",
            description = "Creates a new transaction.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Transaction created successfully",
                            content = @Content(schema = @Schema(implementation = TransactionDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Bank account not found",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    @PostMapping("/create")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('EMPLOYEE')")
    public ResponseEntity<BaseDto> createTransaction(@Valid @RequestBody TransactionRequestDto transaction) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getByEmailRaw(authentication.getName());

        return ResponseEntity.status(201).body(transactionService.create(transaction, user));
    }

}
