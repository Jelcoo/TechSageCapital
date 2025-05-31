package com.techsage.banking.controllers;

import jakarta.transaction.*;
import lombok.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.*;
import org.springframework.test.context.junit.jupiter.*;
import org.springframework.test.web.servlet.request.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Transactional
@Tag("integration")
public abstract class ControllerTestBase {
    @Setter
    private static String jwtToken;

    public static RequestPostProcessor authorized() {
        if (jwtToken == null) {

        }

        return request -> {
            request.addHeader("Authorization", "Bearer " + jwtToken);
            return request;
        };
    }
}
