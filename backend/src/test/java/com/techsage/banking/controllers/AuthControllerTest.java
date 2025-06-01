package com.techsage.banking.controllers;

import com.techsage.banking.models.dto.requests.*;
import org.junit.jupiter.api.*;
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
import com.techsage.banking.models.enums.AuthenticationScope;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest extends ControllerTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    void loginEmployee_Successful() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("johnemployee@example.com");
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

        String jwtToken = JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
        String refreshToken = JsonPath.read(result.getResponse().getContentAsString(), "$.refreshToken");
        setEmployeeToken(jwtToken);
        setRefreshToken(refreshToken);
    }

    @Test
    @Order(1)
    void loginCustomer_Successful() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("johncustomer@example.com");
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

        String jwtToken = JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
        setCustomerToken(jwtToken);
    }

    @Test
    @Order(1)
    void loginAtm_Successful() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("johncustomer@example.com");
        loginRequest.setPassword("password123");
        loginRequest.setCfTurnstileResponse("XXXX.DUMMY.TOKEN.XXXX");
        loginRequest.setAuthenticationScope(AuthenticationScope.ATM.toString());

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isEmpty())
                .andExpect(jsonPath("$.scope").value(AuthenticationScope.ATM.toString()))
                .andReturn();

        String atmToken = JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
        setAtmToken(atmToken);
    }

    /*
     * This code is here to demonstrate that this test was in fact written.
     * Due to a bug, mistake or incompetence by Cloudflare, testing this is not possible.
     * The same test existed for registration, but has been removed.
     * Kind regards, Jelco
     */
    @Test
    @Disabled(value = "Cloudflare always passes in test mode")
    void login_TurnstileVerificationFailed() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("johnadmin@example.com");
        loginRequest.setPassword("password123");
        loginRequest.setCfTurnstileResponse("invalid-turnstile-token");
        loginRequest.setAuthenticationScope(AuthenticationScope.BANK.toString());

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Turnstile verification failed"));
    }

    @Test
    void login_InvalidCredentials() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrong-password");
        loginRequest.setCfTurnstileResponse("valid-turnstile-token");
        loginRequest.setAuthenticationScope(AuthenticationScope.BANK.toString());

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid username/password"));
    }

    @Test
    void register_Successful() throws Exception {
        RegisterRequestDto registerRequest = new RegisterRequestDto();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPhoneNumber("+31612345678");
        registerRequest.setBsn("329459478");
        registerRequest.setPassword("Password123!");
        registerRequest.setConfirmPassword("Password123!");
        registerRequest.setCfTurnstileResponse("XXXX.DUMMY.TOKEN.XXXX");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.scope").value(AuthenticationScope.BANK.toString()));
    }

    @Test
    void refreshToken_Successful() throws Exception {
        RefreshRequestDto refreshRequest = new RefreshRequestDto();
        refreshRequest.setRefreshToken(getRefreshToken());

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.scope").value(AuthenticationScope.BANK.toString()));
    }

    @Test
    void refreshToken_InvalidToken() throws Exception {
        RefreshRequestDto refreshRequest = new RefreshRequestDto();
        refreshRequest.setRefreshToken("invalid-refresh-token");

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid refresh token"));
    }

    @Test
    void login_ValidationError() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("invalid-email");
        loginRequest.setPassword("");
        loginRequest.setCfTurnstileResponse("");
        loginRequest.setAuthenticationScope(null);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.password").exists())
                .andExpect(jsonPath("$.cfTurnstileResponse").exists())
                .andExpect(jsonPath("$.authenticationScope").exists());
    }

    @Test
    void register_ValidationError() throws Exception {
        RegisterRequestDto registerRequest = new RegisterRequestDto();
        registerRequest.setEmail("invalid-email");
        registerRequest.setPhoneNumber("invalid-phone");
        registerRequest.setPassword("weak");
        registerRequest.setConfirmPassword("different");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.phoneNumber").exists())
                .andExpect(jsonPath("$.bsn").exists())
                .andExpect(jsonPath("$.password").exists())
                .andExpect(jsonPath("$.cfTurnstileResponse").exists());
    }
}
