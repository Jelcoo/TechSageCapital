package com.techsage.banking.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsage.banking.models.dto.responses.MessageDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AtmJwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AtmJwtFilter(JwtTokenProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (!isValidAtmToken(token)) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid ATM token");
                return;
            }

            Authentication authentication = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("ATM-Authorization");
        return (bearerToken != null && bearerToken.startsWith("Bearer ")) ?
                bearerToken.substring(7) : null;
    }

    private boolean isValidAtmToken(String token) {
        try {
            Claims claims = jwtProvider.getClaimsFromToken(token);
            String tokenType = claims.get("type", String.class);
            return "atm".equals(tokenType);
        } catch (JwtException e) {
            return false;
        }
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new MessageDto(status, message)));
        response.getWriter().flush();
    }
}
