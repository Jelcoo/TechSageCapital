package com.techsage.banking.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsage.banking.config.*;
import com.techsage.banking.exceptions.TurnstileFailedException;
import com.techsage.banking.jwt.*;
import com.techsage.banking.models.dto.requests.LoginRequestDto;
import com.techsage.banking.models.dto.requests.RefreshRequestDto;
import com.techsage.banking.models.dto.requests.RegisterRequestDto;
import com.techsage.banking.models.dto.responses.AuthResponseDto;
import com.techsage.banking.models.enums.AuthenticationScope;
import com.techsage.banking.services.TurnstileService;
import com.techsage.banking.services.interfaces.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.*;
import org.springframework.test.context.junit.jupiter.*;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@ExtendWith(SpringExtension.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TurnstileService turnstileService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void login_Successful() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
        loginRequest.setCfTurnstileResponse("XXXX.DUMMY.TOKEN.XXXX");
        loginRequest.setAuthenticationScope(AuthenticationScope.BANK.toString());

        AuthResponseDto authResponse = new AuthResponseDto();
        authResponse.setAccessToken("access-token");
        authResponse.setRefreshToken("refresh-token");
        authResponse.setScope(AuthenticationScope.BANK);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.scope").value("BANK"));
    }
} 