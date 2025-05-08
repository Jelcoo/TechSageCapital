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
    private static final long ACCESS_TOKEN_VALIDITY = 3600 * 1000; // 1 hour
    private static final long REFRESH_TOKEN_VALIDITY = 30 * 24 * 3600 * 1000; // 30 days

    public JwtTokenProvider(JwtKeyProvider keyProvider, UserDetailsServiceJpa userDetailsService) {
        this.keyProvider = keyProvider;
        this.userDetailsService = userDetailsService;
    }

    public String createAccessToken(String username, List<UserRole> roles) throws JwtException {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_VALIDITY);
        Key privateKey = keyProvider.getPrivateKey();

        return Jwts.builder()
                .subject(username)
                .claim("auth", roles.stream().map(UserRole::name).toList())
                .claim("type", "access")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(privateKey)
                .compact();
    }

    public String createRefreshToken(String username) throws JwtException {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_VALIDITY);
        Key privateKey = keyProvider.getPrivateKey();

        return Jwts.builder()
                .subject(username)
                .claim("type", "refresh")
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
            String tokenType = claims.get("type", String.class);

            if (!tokenType.equals("access")) {
                throw new JwtException("Invalid token type");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(userDetails, "",
                    userDetails.getAuthorities());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Bearer token not valid");
        }
    }

    public String getUsernameFromToken(String token) {
        PublicKey publicKey = keyProvider.getPublicKey();
        try {
            Claims claims = Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Token not valid");
        }
    }

    public boolean validateToken(String token) {
        PublicKey publicKey = keyProvider.getPublicKey();
        try {
            Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
