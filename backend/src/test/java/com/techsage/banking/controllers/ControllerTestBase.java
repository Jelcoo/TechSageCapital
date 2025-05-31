package com.techsage.banking.controllers;

import lombok.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.suite.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Tag("integration")
@Suite
@SelectClasses({
        AuthControllerTest.class,
        UserControllerTest.class
})
public abstract class ControllerTestBase {
    @Setter
    private static String jwtToken;

    @Setter
    @Getter
    private static String refreshToken;

    public static RequestPostProcessor authorized() {
        if (jwtToken == null) {
            throw new IllegalStateException("JWT token not set. Make sure AuthControllerTest.login_Successful runs first.");
        }

        return request -> {
            request.addHeader("Authorization", "Bearer " + jwtToken);
            return request;
        };
    }
}
