package com.techsage.banking.controllers;


import com.techsage.banking.exceptions.TransactionException;
import com.techsage.banking.models.Transaction;
import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.BaseDto;
import com.techsage.banking.models.dto.TransactionDto;
import com.techsage.banking.models.dto.requests.TransactionRequestDto;
import com.techsage.banking.models.dto.responses.MessageDto;
import com.techsage.banking.services.interfaces.TransactionService;
import com.techsage.banking.services.interfaces.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.iban4j.IbanFormatException;
import org.iban4j.InvalidCheckDigitException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transactions", description = "Endpoints for transaction management")
public class TransactionController {
    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public Transaction getTransaction(long id) {
        return transactionService.getById(id);
    }

    @GetMapping
    public List<TransactionDto> getTransactions() {
        return transactionService.getAll();
    }

    @PostMapping("/create")
    public ResponseEntity<BaseDto> CreateTransaction(@RequestBody TransactionRequestDto transaction) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getByEmailRaw(authentication.getName());
        try {
            return ResponseEntity.status(201).body(transactionService.create(transaction, user));
        } catch (TransactionException e) {
            if (e.getReason() == TransactionException.Reason.BANK_ACCOUNT_NOT_FOUND) {
                return ResponseEntity.status(404).body(new MessageDto(404, e.getReason().getMessage()));
            }
            return ResponseEntity.badRequest().body(new MessageDto(400, e.getReason().getMessage()));
        } catch (InvalidCheckDigitException | IbanFormatException e) {
            return ResponseEntity.status(400).body(new MessageDto(400, "Invalid IBAN"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageDto(500, "Internal server error"));
        }
    }

}
