package com.techsage.banking.helpers;

import com.digitalsanctuary.cf.turnstile.service.*;
import jakarta.servlet.http.*;

public class TurnstileHelper {
    private final TurnstileValidationService turnstileValidationService;

    public TurnstileHelper(TurnstileValidationService turnstileValidationService) {
        this.turnstileValidationService = turnstileValidationService;
    }

    public boolean validateTurnstile(HttpServletRequest request, String token) {
        String clientIpAddress = turnstileValidationService.getClientIpAddress(request);

        return turnstileValidationService.validateTurnstileResponse(token, clientIpAddress);
    }
}
