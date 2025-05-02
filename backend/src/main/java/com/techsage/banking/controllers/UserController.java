package com.techsage.banking.controllers;

import com.techsage.banking.models.dto.UserDto;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.services.interfaces.UserService;
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserDto me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.getByEmail(authentication.getName());
    }

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<UserDto> getAll(@RequestParam(defaultValue = "ACTIVE") UserStatus status) {
        return userService.findByStatus(status);
    }
}
