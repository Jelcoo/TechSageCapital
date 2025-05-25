package com.techsage.banking.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsage.banking.models.dto.responses.MessageDto;
import com.techsage.banking.models.enums.AuthenticationClaim;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
public class ClaimAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public ClaimAuthorizationFilter(JwtTokenProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    // Routes accessible only by ATM tokens
    private final Map<String, Set<String>> atmAllowedRoutes = Map.of(
            "/atm/withdraw", Set.of(HttpMethod.POST.name()),
            "/atm/deposit", Set.of(HttpMethod.POST.name()),
            "/users/me", Set.of(HttpMethod.GET.name()),
            "/bankAccounts", Set.of(HttpMethod.GET.name())
    );

    // Routes disallowed for ACCESS tokens
    private final Map<String, Set<String>> accessDisallowedRoutes = Map.of(
            "/atm/withdraw", Set.of(HttpMethod.POST.name()),
            "/atm/deposit", Set.of(HttpMethod.POST.name())
    );

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
            tokenType = AuthenticationClaim.valueOf(claims.get("type", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Validate access based on token type and rules
        if (tokenType == AuthenticationClaim.ATM_TOKEN && !isAllowed(atmAllowedRoutes, path, method)) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied for ATM token");
            return;
        } else if (tokenType == AuthenticationClaim.ACCESS_TOKEN && isDisallowed(accessDisallowedRoutes, path, method)) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied for ACCESS token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAllowed(Map<String, Set<String>> rules, String path, String method) {
        return rules.entrySet().stream().anyMatch(entry ->
                pathMatcher.match(entry.getKey(), path) && entry.getValue().contains(method)
        );
    }

    private boolean isDisallowed(Map<String, Set<String>> rules, String path, String method) {
        return rules.entrySet().stream().anyMatch(entry ->
                pathMatcher.match(entry.getKey(), path) && entry.getValue().contains(method)
        );
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
