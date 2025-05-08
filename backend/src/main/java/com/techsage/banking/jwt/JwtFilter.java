package com.techsage.banking.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsage.banking.models.dto.responses.MessageDto;
import io.jsonwebtoken.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.stereotype.*;
import org.springframework.web.filter.*;

import java.io.*;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtProvider;
    private final ObjectMapper objectMapper;

    public JwtFilter(JwtTokenProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Authentication authentication = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(new MessageDto(401, "Invalid JWT token")));
            response.getWriter().flush();
            return;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(new MessageDto(500, "Internal server error")));
            response.getWriter().flush();
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
