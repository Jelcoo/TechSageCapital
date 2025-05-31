package com.techsage.banking.controllers;

import com.techsage.banking.exceptions.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.techsage.banking.models.dto.requests.LoginRequestDto;
import com.techsage.banking.models.enums.AuthenticationScope;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(1)
class AuthControllerTest extends ControllerTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    void login_Successful() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("johnadmin@example.com");
        loginRequest.setPassword("password123");
        loginRequest.setCfTurnstileResponse("XXXX.DUMMY.TOKEN.XXXX");
        loginRequest.setAuthenticationScope(AuthenticationScope.BANK.toString());

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.scope").value(AuthenticationScope.BANK.toString()))
                .andReturn();

        String token = JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
        setJwtToken(token);
    }

    @Test
    void login_TurnstileVerificationFailed() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("johnadmin@example.com");
        loginRequest.setPassword("password123");
        loginRequest.setCfTurnstileResponse("invalid-turnstile-token");
        loginRequest.setAuthenticationScope(AuthenticationScope.BANK.toString());

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Turnstile verification failed"));
    }
//
//    @Test
//    void login_InvalidCredentials() throws Exception {
//        // Arrange
//        LoginRequestDto loginRequest = new LoginRequestDto();
//        loginRequest.setEmail("test@example.com");
//        loginRequest.setPassword("wrong-password");
//        loginRequest.setCfTurnstileResponse("valid-turnstile-token");
//        loginRequest.setAuthenticationScope(AuthenticationScope.BANK.toString());
//
//        doNothing().when(turnstileService).verifyToken(any());
//        when(userService.login(any())).thenThrow(new AuthenticationException("Invalid credentials"));
//
//        // Act & Assert
//        mockMvc.perform(post("/auth/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(loginRequest)))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.status").value(401))
//                .andExpect(jsonPath("$.message").value("Invalid credentials"));
//
//        verify(turnstileService).verifyToken("valid-turnstile-token");
//        verify(userService).login(any(LoginRequestDto.class));
//    }
//
//    @Test
//    void register_Successful() throws Exception {
//        // Arrange
//        RegisterRequestDto registerRequest = new RegisterRequestDto();
//        registerRequest.setFirstName("John");
//        registerRequest.setLastName("Doe");
//        registerRequest.setEmail("john.doe@example.com");
//        registerRequest.setPhoneNumber("+31612345678");
//        registerRequest.setBsn("123456789");
//        registerRequest.setPassword("Password123!");
//        registerRequest.setConfirmPassword("Password123!");
//        registerRequest.setCfTurnstileResponse("valid-turnstile-token");
//
//        AuthResponseDto authResponse = new AuthResponseDto();
//        authResponse.setAccessToken("access-token");
//        authResponse.setRefreshToken("refresh-token");
//        authResponse.setScope(AuthenticationScope.BANK);
//
//        doNothing().when(turnstileService).verifyToken(any());
//        when(userService.register(any())).thenReturn(authResponse);
//
//        // Act & Assert
//        mockMvc.perform(post("/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(registerRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.accessToken").value("access-token"))
//                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
//                .andExpect(jsonPath("$.scope").value("BANK"));
//
//        verify(turnstileService).verifyToken("valid-turnstile-token");
//        verify(userService).register(any(RegisterRequestDto.class));
//    }
//
//    @Test
//    void register_TurnstileVerificationFailed() throws Exception {
//        // Arrange
//        RegisterRequestDto registerRequest = new RegisterRequestDto();
//        registerRequest.setFirstName("John");
//        registerRequest.setLastName("Doe");
//        registerRequest.setEmail("john.doe@example.com");
//        registerRequest.setPhoneNumber("+31612345678");
//        registerRequest.setBsn("123456789");
//        registerRequest.setPassword("Password123!");
//        registerRequest.setConfirmPassword("Password123!");
//        registerRequest.setCfTurnstileResponse("invalid-turnstile-token");
//
//        doThrow(new TurnstileFailedException("Invalid turnstile token")).when(turnstileService).verifyToken(any());
//
//        // Act & Assert
//        mockMvc.perform(post("/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(registerRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.status").value(400))
//                .andExpect(jsonPath("$.message").value("Invalid turnstile token"));
//
//        verify(turnstileService).verifyToken("invalid-turnstile-token");
//        verify(userService, never()).register(any());
//    }
//
//    @Test
//    void refreshToken_Successful() throws Exception {
//        // Arrange
//        RefreshRequestDto refreshRequest = new RefreshRequestDto();
//        refreshRequest.setRefreshToken("valid-refresh-token");
//
//        AuthResponseDto authResponse = new AuthResponseDto();
//        authResponse.setAccessToken("new-access-token");
//        authResponse.setRefreshToken("new-refresh-token");
//        authResponse.setScope(AuthenticationScope.BANK);
//
//        when(userService.refreshToken(any())).thenReturn(authResponse);
//
//        // Act & Assert
//        mockMvc.perform(post("/auth/refresh")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(refreshRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
//                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"))
//                .andExpect(jsonPath("$.scope").value("BANK"));
//
//        verify(userService).refreshToken(any(RefreshRequestDto.class));
//    }
//
//    @Test
//    void refreshToken_InvalidToken() throws Exception {
//        // Arrange
//        RefreshRequestDto refreshRequest = new RefreshRequestDto();
//        refreshRequest.setRefreshToken("invalid-refresh-token");
//
//        when(userService.refreshToken(any())).thenThrow(new AuthenticationException("Invalid refresh token"));
//
//        // Act & Assert
//        mockMvc.perform(post("/auth/refresh")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(refreshRequest)))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.status").value(401))
//                .andExpect(jsonPath("$.message").value("Invalid refresh token"));
//
//        verify(userService).refreshToken(any(RefreshRequestDto.class));
//    }
//
//    @Test
//    void login_ValidationError() throws Exception {
//        // Arrange
//        LoginRequestDto loginRequest = new LoginRequestDto();
//        loginRequest.setEmail("invalid-email");
//        loginRequest.setPassword("");
//        loginRequest.setCfTurnstileResponse("");
//        loginRequest.setAuthenticationScope(null);
//
//        // Act & Assert
//        mockMvc.perform(post("/auth/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(loginRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.email").exists())
//                .andExpect(jsonPath("$.password").exists())
//                .andExpect(jsonPath("$.cf-turnstile-response").exists())
//                .andExpect(jsonPath("$.scope").exists());
//
//        verify(turnstileService, never()).verifyToken(any());
//        verify(userService, never()).login(any());
//    }
//
//    @Test
//    void register_ValidationError() throws Exception {
//        // Arrange
//        RegisterRequestDto registerRequest = new RegisterRequestDto();
//        // Missing required fields and invalid values
//        registerRequest.setEmail("invalid-email");
//        registerRequest.setPhoneNumber("invalid-phone");
//        registerRequest.setPassword("weak");
//        registerRequest.setConfirmPassword("different");
//
//        // Act & Assert
//        mockMvc.perform(post("/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(registerRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.firstName").exists())
//                .andExpect(jsonPath("$.lastName").exists())
//                .andExpect(jsonPath("$.email").exists())
//                .andExpect(jsonPath("$.phoneNumber").exists())
//                .andExpect(jsonPath("$.bsn").exists())
//                .andExpect(jsonPath("$.password").exists())
//                .andExpect(jsonPath("$.cf-turnstile-response").exists());
//
//        verify(turnstileService, never()).verifyToken(any());
//        verify(userService, never()).register(any());
//    }
} 