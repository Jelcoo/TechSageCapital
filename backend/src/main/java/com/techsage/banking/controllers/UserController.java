package com.techsage.banking.controllers;

import com.techsage.banking.models.dto.BaseDto;
import com.techsage.banking.models.dto.UserDto;
import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.models.dto.responses.*;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.services.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.*;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Endpoints for user management")
public class UserController extends BaseController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Current user",
            description = "Retrieves the current authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval",
                            content = @Content(schema = @Schema(implementation = UserDto.class))
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
                            description = "User not found",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public UserDto me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.getByEmail(authentication.getName());
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves all users with a specific status.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserPagedDto.class)))
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
    public PageResponseDto<UserDto> getAll(@RequestParam(defaultValue = "ACTIVE") UserStatus status, @ParameterObject Pageable pageable) {
        Page<UserDto> page = userService.findByStatus(status, pageable);
        return new PageResponseDto<>(page);
    }

    @Operation(
            summary = "Soft delete user",
            description = "Soft deletes a user by their ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful deletion",
                            content = @Content(schema = @Schema(implementation = UserDto.class))
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
                            description = "User not found",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    @DeleteMapping("/{id}/softDelete")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BaseDto> softDeleteUser(@PathVariable long id) {
        try {
            return ResponseEntity.ok().body(userService.softDelete(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageDto(400, e.getMessage()));
        }
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a user by their ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval",
                            content = @Content(schema = @Schema(implementation = UserDto.class))
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
                            description = "User not found",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BaseDto> getById(@PathVariable long id) {
        try {
            return ResponseEntity.ok().body(userService.getById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageDto(400, e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(new MessageDto(404, e.getMessage()));
        }
    }

    @Operation(
            summary = "Approve user",
            description = "Approves a user and returns the new user object.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful approval",
                            content = @Content(schema = @Schema(implementation = UserDto.class))
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
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BaseDto> approveUser(@PathVariable long id, @Valid @RequestBody ApprovalRequestDto approvalRequestDto) {
        try {
            return ResponseEntity.ok().body(userService.approveUser(id, approvalRequestDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageDto(400, e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(new MessageDto(404, e.getMessage()));
        }
    }

    @Operation(
            summary = "Edit user status",
            description = "Edits a users status and returns a 200 status code.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful update",
                            content = @Content(schema = @Schema(implementation = UserDto.class))
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
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    @PutMapping("/{id}/reinstate")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BaseDto> reinstateUser(@PathVariable long id) {
        try {
            return ResponseEntity.ok().body(userService.reinstateUser(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageDto(400, e.getMessage()));
        }
    }

    @Operation(
            summary = "Edit user information",
            description = "Edits a user and returns a 200 status code.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful update",
                            content = @Content(schema = @Schema(implementation = UserDto.class))
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
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    @PutMapping("/{id}/update")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BaseDto> updateUser(@PathVariable long id,@Valid @RequestBody UpdateUserRequestDto userDto) {
        try {
            return ResponseEntity.ok().body(userService.update(id, userDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageDto(400, e.getMessage()));
        }
    }

    @Operation(
            summary = "Edit customers own information",
            description = "Edits a customers own information and returns a 200 status code.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful update",
                            content = @Content(schema = @Schema(implementation = UserDto.class))
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
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    @PutMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseDto> updateSelf(@Valid @RequestBody UpdateSelfRequestDto requestBody) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentEmail = authentication.getName();
            return ResponseEntity.ok().body(userService.updateSelf(currentEmail, requestBody));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageDto(400, e.getMessage()));
        }
    }

    @Operation(
            summary = "Edit password",
            description = "Edits a password and returns a 200 status code.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful update",
                            content = @Content(schema = @Schema(implementation = UserDto.class))
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
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    @PutMapping("/{id}/updatePassword")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BaseDto> updatePassword(@Valid @RequestBody UpdateUserPasswordRequestDto requestBody, @PathVariable long id) {
        try{
            return ResponseEntity.ok().body(userService.updatePassword(id, requestBody));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageDto(400, e.getMessage()));
        }
    }

    @Operation(
            summary = "Edit password",
            description = "Edits a password and returns a 200 status code.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful update",
                            content = @Content(schema = @Schema(implementation = UserDto.class))
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
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    @PutMapping("/me/updatePassword")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseDto> updateOwnPassword(@Valid @RequestBody UpdatePasswordRequestDto requestBody) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            return ResponseEntity.ok().body(userService.updateOwnPassword(email, requestBody));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageDto(400, e.getMessage()));
        }
    }

}
