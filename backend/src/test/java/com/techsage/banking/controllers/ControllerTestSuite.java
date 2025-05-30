package com.techsage.banking.controllers;

import lombok.*;
import org.junit.jupiter.api.*;
import org.junit.platform.suite.api.*;
import org.springframework.test.web.servlet.request.*;

@Suite
@SelectClasses({
        AuthControllerTest.class,
        UserControllerTest.class
})
@Tag("integration-suite")
public class ControllerTestSuite {
    @Setter
    private static String jwtToken;

    public static RequestPostProcessor authorized() {
        return request -> {
            request.addHeader("Authorization", "Bearer " + jwtToken);
            return request;
        };
    }
}
