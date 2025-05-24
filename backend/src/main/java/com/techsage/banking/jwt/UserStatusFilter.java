package com.techsage.banking.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsage.banking.models.dto.responses.MessageDto;
import com.techsage.banking.models.enums.UserStatus;
import com.techsage.banking.services.UserServiceJpa;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UserStatusFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Skip filter if no authentication
        if (authentication == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get UserServiceJpa lazily when needed
        UserServiceJpa userService = applicationContext.getBean(UserServiceJpa.class);

        // Check if user is deleted
        var user = userService.getByEmailRaw(authentication.getName());
        if (user != null && user.getStatus() == UserStatus.DELETED) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, "Account is not accessible");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new MessageDto(status, message)));
        response.getWriter().flush();
    }
} 