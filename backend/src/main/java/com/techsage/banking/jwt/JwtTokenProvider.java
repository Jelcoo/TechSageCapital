package com.techsage.banking.jwt;

import com.techsage.banking.models.enums.*;
import com.techsage.banking.services.*;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;

import java.security.*;
import java.util.*;

@Component
public class JwtTokenProvider {
    private final JwtKeyProvider keyProvider;
    private final UserDetailsServiceJpa userDetailsService;

    public JwtTokenProvider(JwtKeyProvider keyProvider, UserDetailsServiceJpa userDetailsService) {
        this.keyProvider = keyProvider;
        this.userDetailsService = userDetailsService;
    }

    public String createToken(String username, List<UserRole> roles) throws JwtException {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600000);
        Key privateKey = keyProvider.getPrivateKey();

        return Jwts.builder()
                .subject(username)
                .claim("auth", roles.stream().map(UserRole::name).toList())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(privateKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        PublicKey publicKey = keyProvider.getPublicKey();
        try {
            Claims claims = Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload();
            String username = claims.getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(userDetails, "",
                    userDetails.getAuthorities());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Bearer token not valid");
        }
    }
}
