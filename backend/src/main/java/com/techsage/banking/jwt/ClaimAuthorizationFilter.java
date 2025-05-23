package com.techsage.banking.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsage.banking.models.dto.responses.MessageDto;
import com.techsage.banking.models.enums.*;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
public class ClaimAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Set<String> ATM_ALLOWED = Set.of("/atm/withdraw", "/atm/deposit", "/users/me", "/bankAccounts");
    private final Set<String> ACCESS_DISALLOWED = Set.of("/atm/withdraw", "/atm/deposit");

    public ClaimAuthorizationFilter(JwtTokenProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = getToken(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        AuthenticationClaim tokenType;
        try {
            Claims claims = jwtProvider.getClaimsFromToken(token);
            String typeString = claims.get("type", String.class);
            tokenType = AuthenticationClaim.valueOf(typeString);
        } catch (JwtException e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }

        String path = request.getRequestURI();

        // Claim rules
        if (tokenType == AuthenticationClaim.ATM_TOKEN && ATM_ALLOWED.stream().noneMatch(path::matches)) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        } else if (tokenType == AuthenticationClaim.ACCESS_TOKEN && ACCESS_DISALLOWED.stream().anyMatch(path::matches)) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new MessageDto(status, message)));
    }
}
