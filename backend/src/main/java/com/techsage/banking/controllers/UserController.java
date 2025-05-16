package com.techsage.banking.controllers;

import com.techsage.banking.models.dto.BaseDto;
import com.techsage.banking.models.dto.UserDto;
import com.techsage.banking.models.dto.requests.ApprovalRequestDto;
import com.techsage.banking.models.dto.requests.UserLimitsRequestDto;
import com.techsage.banking.models.dto.responses.MessageDto;
import com.techsage.banking.models.dto.updateUserDto;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.services.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
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
    public List<UserDto> getAll(@RequestParam(defaultValue = "ACTIVE") UserStatus status) {
        return userService.findByStatus(status);
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
    public void softDeleteUser(@PathVariable long id) {
        userService.softDelete(id);
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
    @PreAuthorize("hasRole('EMPLOYEE')" + " or hasRole('CUSTOMER')")
    public UserDto getById(@PathVariable long id) {
        return userService.getById(id);
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
        catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageDto(500, e.getMessage()));
        }
    }

    @Operation(
            summary = "Edit user transaction limits",
            description = "Approves a user and returns a 200 status code.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful update",
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
    @PutMapping("/{id}/limits")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BaseDto> updateLimits(@PathVariable long id, @Valid @RequestBody UserLimitsRequestDto userLimitsRequestDto) {
        try {
            return ResponseEntity.ok().body(userService.updateLimits(id, userLimitsRequestDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageDto(400, e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageDto(500, e.getMessage()));
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
        try{
            return ResponseEntity.ok().body(userService.reinstateUser(id));
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageDto(400, e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageDto(500, e.getMessage()));
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
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BaseDto> UpdateUser(@PathVariable long id,@Valid @RequestBody updateUserDto userDto) {
        try{
            return ResponseEntity.ok().body(userService.update(id, userDto));
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageDto(400, e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageDto(500, e.getMessage()));
        }
    }


}
