package com.techsage.banking.controllers;

import org.springframework.http.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

public class BaseController {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}
