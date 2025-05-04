package com.techsage.banking.controllers;

import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.BaseDto;
import com.techsage.banking.models.dto.UserDto;
import com.techsage.banking.models.dto.requests.ApprovalRequestDto;
import com.techsage.banking.models.dto.responses.MessageDto;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.services.interfaces.BankAccountService;
import com.techsage.banking.services.interfaces.UserService;
import jakarta.validation.Valid;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

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

    @GetMapping("/accountdetails/{accountId}")
    public UserDto get(@PathVariable long accountId) {
        return userService.getById(accountId);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<UserDto> getAllUsers() {
        return userService.getAll();
    }

    @PutMapping("/softDelete/{accountId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public void softDeleteUser(@PathVariable long accountId) {
        userService.softDelete(accountId);
    }

    @GetMapping("/getById/{ID}")
    public UserDto getById(@PathVariable long ID) {
        return userService.getById(ID);
    }

    @PostMapping("{id}/approve")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BaseDto> approveUser(@PathVariable long id, @Valid @RequestBody ApprovalRequestDto approvalRequestDto) {
        try {
            return ResponseEntity.ok().body(userService.approveUser(id, approvalRequestDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageDto(400, e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(new MessageDto(404, e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageDto(500, e.getMessage()));
        }
    }
}
