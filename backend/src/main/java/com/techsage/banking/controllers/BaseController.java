package com.techsage.banking.controllers;

import com.techsage.banking.models.dto.*;
import com.techsage.banking.models.dto.responses.*;
import org.springframework.http.*;
import org.springframework.security.authorization.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.*;

import java.util.*;

@ControllerAdvice
public class BaseController {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<BaseDto> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        return ResponseEntity.status(401).body(new MessageDto(401, "Unauthorized, please log in"));
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
