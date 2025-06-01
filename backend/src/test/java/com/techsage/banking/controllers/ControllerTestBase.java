package com.techsage.banking.controllers;

import com.fasterxml.jackson.databind.*;
import com.techsage.banking.repositories.*;
import lombok.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public abstract class ControllerTestBase {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected BankAccountRepository bankAccountRepository;

    @Setter
    private static String customerToken;

    @Setter
    private static String employeeToken;

    @Setter
    private static String atmToken;

    @Setter
    @Getter
    private static String refreshToken;

    public static RequestPostProcessor authorized(AuthMethod method) {
        String token = switch (method) {
            case CUSTOMER -> customerToken;
            case EMPLOYEE -> employeeToken;
            case ATM -> atmToken;
        };

        if (token == null) {
            throw new IllegalStateException("JWT token not set. Make sure AuthControllerTest runs first.");
        }

        return request -> {
            request.addHeader("Authorization", "Bearer " + token);
            return request;
        };
    }

    public enum AuthMethod {
        CUSTOMER,
        EMPLOYEE,
        ATM;
    }
}
