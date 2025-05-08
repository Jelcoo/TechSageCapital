package com.techsage.banking.services;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class TurnstileService {

    @Value("${turnstile.secret-key}")
    private String secretKey;

    private static final String VERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    public boolean verifyToken(String token) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", secretKey);
        params.add("response", token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<TurnstileResponse> response = restTemplate.postForEntity(
                VERIFY_URL, request, TurnstileResponse.class);

        return response.getBody() != null && response.getBody().isSuccess();
    }

    @Data
    private static class TurnstileResponse {
        private boolean success;
        private String[] errorCodes;
    }
}
