package com.techsage.banking.controllers;

import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.UserDto;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.services.interfaces.UserService;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RestController
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<UserDto> getAll(@RequestParam(defaultValue = "ACTIVE") UserStatus status) {
        return userService.findAllAccountsByStatus(status);
    }
}
