package com.techsage.banking.services;

import com.techsage.banking.exceptions.TurnstileFailedException;
import com.techsage.banking.services.TurnstileService.TurnstileResponse;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Tag("unit")
class TurnstileServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TurnstileService turnstileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void verifyToken_success_doesNotThrow() {
        // Arrange
        TurnstileResponse successResponse = new TurnstileResponse();
        successResponse.setSuccess(true);
        ResponseEntity<TurnstileResponse> response = new ResponseEntity<>(successResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(TurnstileResponse.class)))
                .thenReturn(response);

        // Act & Assert
        assertDoesNotThrow(() -> turnstileService.verifyToken("valid-token"));
    }

    @Test
    void verifyToken_failure_throwsTurnstileFailedException() {
        // Arrange
        TurnstileResponse failResponse = new TurnstileResponse();
        failResponse.setSuccess(false);
        ResponseEntity<TurnstileResponse> response = new ResponseEntity<>(failResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(TurnstileResponse.class)))
                .thenReturn(response);

        // Act & Assert
        assertThrows(TurnstileFailedException.class, () -> turnstileService.verifyToken("invalid-token"));
    }

    @Test
    void verifyToken_nullBody_throwsTurnstileFailedException() {
        // Arrange
        ResponseEntity<TurnstileResponse> response = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(TurnstileResponse.class)))
                .thenReturn(response);

        // Act & Assert
        assertThrows(TurnstileFailedException.class, () -> turnstileService.verifyToken("any-token"));
    }
}
