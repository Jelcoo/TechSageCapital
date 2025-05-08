package com.techsage.banking.filter;

import com.techsage.banking.services.TurnstileService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class TurnstileFilter extends OncePerRequestFilter {
    private final TurnstileService turnstileService;
    private static final List<String> PROTECTED_ENDPOINTS = Arrays.asList("/auth/login", "/auth/register");

    public TurnstileFilter(TurnstileService turnstileService) {
        this.turnstileService = turnstileService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

        if (PROTECTED_ENDPOINTS.contains(path)) {
            String turnstileToken = request.getParameter("cf-turnstile-response");

            if (turnstileToken == null || !turnstileService.verifyToken(turnstileToken)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid Turnstile token");
                response.getWriter().flush();
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
