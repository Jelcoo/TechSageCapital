package com.techsage.banking.services;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class TurnstileService {
    @Value("${turnstile.secret-key}")
    private String secretKey;

    private final RestTemplate restTemplate;
    private static final String VERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    public TurnstileService() {
        this.restTemplate = new RestTemplate();
    }

    public boolean verifyToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("secret", secretKey);
        map.add("response", token);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            TurnstileResponse response = restTemplate.postForObject(VERIFY_URL, request, TurnstileResponse.class);
            return response != null && response.isSuccess();
        } catch (Exception e) {
            return false;
        }
    }

    @Data
    private static class TurnstileResponse {
        private boolean success;
        private String[] errorCodes;
    }
}
