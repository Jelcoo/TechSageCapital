package com.techsage.banking.controllers;

import com.techsage.banking.models.dto.UserDto;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.services.interfaces.UserService;
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.web.bind.annotation.*;
import com.techsage.banking.models.User;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
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
    @PreAuthorize("hasRole('EMPLOYEE')")  // maybe add || hasRole('ADMIN') as well.
    public List<UserDto> getAll(@RequestParam(defaultValue = "ACTIVE") UserStatus status) {
        return userService.findByStatus(status);
    }

    @GetMapping("/accountdetails/{accountId}")
    public User get(@PathVariable long accountId) {
        return userService.getById(accountId);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasRole('EMPLOYEE')|| hasRole('ADMIN')")
    public List<UserDto> getAllUsers(){
        return userService.getAll();
    }

   @PatchMapping("/softDelete/{accountId}")
   @PreAuthorize("hasRole('EMPLOYEE')|| hasRole('ADMIN')")
   public void softDeleteUser(@PathVariable long accountId) {
       User user = userService.getById(accountId);
       user.setStatus(UserStatus.DELETED);
       userService.update(user);
   }
}
