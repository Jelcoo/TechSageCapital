package com.techsage.banking.services;

import com.techsage.banking.exceptions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.test.util.*;
import org.springframework.web.client.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TurnstileServiceTest {

    private static final String VERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    @Test
    void verifyToken_success_doesNotThrow() {
        // prepare a mock RestTemplate that returns success = true
        try (MockedConstruction<RestTemplate> mocked =
                     Mockito.mockConstruction(org.springframework.web.client.RestTemplate.class,
                             (mock, context) -> {
                                 TurnstileService.TurnstileResponse body = new TurnstileService.TurnstileResponse();
                                 body.setSuccess(true);
                                 ResponseEntity<TurnstileService.TurnstileResponse> respEntity =
                                         new ResponseEntity<>(body, HttpStatus.OK);
                                 when(mock.postForEntity(eq(VERIFY_URL), any(HttpEntity.class), eq(TurnstileService.TurnstileResponse.class)))
                                         .thenReturn(respEntity);
                             })) {
            TurnstileService service = new TurnstileService();
            // inject the secretKey
            ReflectionTestUtils.setField(service, "secretKey", "secret-value");

            // should not throw
            assertDoesNotThrow(() -> service.verifyToken("dummy-token"));
        }
    }

    @Test
    void verifyToken_failure_throwsException() {
        // prepare a mock RestTemplate that returns success = false
        try (MockedConstruction<org.springframework.web.client.RestTemplate> mocked =
                     Mockito.mockConstruction(org.springframework.web.client.RestTemplate.class,
                             (mock, context) -> {
                                 TurnstileService.TurnstileResponse body = new TurnstileService.TurnstileResponse();
                                 body.setSuccess(false);
                                 ResponseEntity<TurnstileService.TurnstileResponse> respEntity =
                                         new ResponseEntity<>(body, HttpStatus.OK);
                                 when(mock.postForEntity(eq(VERIFY_URL), any(HttpEntity.class), eq(TurnstileService.TurnstileResponse.class)))
                                         .thenReturn(respEntity);
                             })) {
            TurnstileService service = new TurnstileService();
            ReflectionTestUtils.setField(service, "secretKey", "secret-value");

            assertThrows(TurnstileFailedException.class, () -> service.verifyToken("dummy-token"));
        }
    }


    @Test
    void verifyToken_nullBody_throwsException() {
        // mock RestTemplate returning null body
        try (MockedConstruction<org.springframework.web.client.RestTemplate> mocked =
                     Mockito.mockConstruction(org.springframework.web.client.RestTemplate.class,
                             (mock, context) -> {
                                 ResponseEntity<TurnstileService.TurnstileResponse> respEntity =
                                         new ResponseEntity<>(null, HttpStatus.OK);
                                 when(mock.postForEntity(eq(VERIFY_URL), any(HttpEntity.class), eq(TurnstileService.TurnstileResponse.class)))
                                         .thenReturn(respEntity);
                             })) {
            TurnstileService service = new TurnstileService();
            ReflectionTestUtils.setField(service, "secretKey", "secret-value");

            assertThrows(TurnstileFailedException.class, () -> service.verifyToken("dummy-token"));
        }
    }
}