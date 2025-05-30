package com.techsage.banking.services;

import com.techsage.banking.exceptions.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;
import org.springframework.web.client.*;

@Service
public class TurnstileService {

    @Value("${turnstile.secret-key}")
    private String secretKey;

    private final RestTemplate restTemplate;
    private static final String VERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    public TurnstileService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean verifyToken(String token) throws TurnstileFailedException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", secretKey);
        params.add("response", token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<TurnstileResponse> response = restTemplate.postForEntity(VERIFY_URL, request, TurnstileResponse.class);

        if (response.getBody() == null || !response.getBody().isSuccess()) {
            throw new TurnstileFailedException();
        }

        return true;
    }

    @Data
    public static class TurnstileResponse {
        private boolean success;
        private String[] errorCodes;
    }
}
