package com.techsage.banking.controllers;

import com.techsage.banking.exceptions.*;
import com.techsage.banking.models.dto.*;
import com.techsage.banking.models.dto.responses.*;
import org.springframework.http.*;
import org.springframework.security.authorization.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.*;

import javax.naming.*;
import java.util.*;

@ControllerAdvice
public class BaseController {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(400).body(errors);
    }

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<BaseDto> handleTransactionException(TransactionException ex) {
        if (ex.getReason() == TransactionException.Reason.BANK_ACCOUNT_NOT_FOUND) {
            return ResponseEntity.status(404).body(new MessageDto(404, ex.getReason().getMessage()));
        }
        return ResponseEntity.badRequest().body(new MessageDto(400, ex.getReason().getMessage()));
    }

    @ExceptionHandler(TurnstileFailedException.class)
    public ResponseEntity<BaseDto> handleTurnstileFailedException(TurnstileFailedException ex) {
        return ResponseEntity.status(400).body(new MessageDto(400, ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(400).body(new MessageDto(400, ex.getMessage()));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<BaseDto> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        return ResponseEntity.status(401).body(new MessageDto(401, "Unauthorized, please log in"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseDto> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity.status(401).body(new MessageDto(401, ex.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BaseDto> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity.status(404).body(new MessageDto(404, "Not found"));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<BaseDto> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity.status(404).body(new MessageDto(404, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseDto> handleException(Exception ex) {
        ex.printStackTrace(); // consider logging instead
        return ResponseEntity.status(500).body(new MessageDto(500, "Internal server error"));
    }
}
