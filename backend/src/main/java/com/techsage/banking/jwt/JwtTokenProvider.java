package com.techsage.banking.jwt;

import com.techsage.banking.models.enums.*;
import com.techsage.banking.services.UserDetailsServiceJpa;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private final JwtKeyProvider keyProvider;
    private final UserDetailsServiceJpa userDetailsService;

    private static final long ACCESS_TOKEN_VALIDITY = 60 * 60 * 1000L; // 1 hour
    private static final long ATM_TOKEN_VALIDITY = 10 * 60 * 1000L; // 10 minutes
    private static final long REFRESH_TOKEN_VALIDITY = 30 * 24 * 60 * 60 * 1000L; // 30 days

    public JwtTokenProvider(JwtKeyProvider keyProvider, UserDetailsServiceJpa userDetailsService) {
        this.keyProvider = keyProvider;
        this.userDetailsService = userDetailsService;
    }

    public String createAccessToken(String username, List<UserRole> roles) {
        return createToken(username, AuthenticationClaim.ACCESS_TOKEN, ACCESS_TOKEN_VALIDITY, roles);
    }

    public String createAtmToken(String username, List<UserRole> roles) {
        return createToken(username, AuthenticationClaim.ATM_TOKEN, ATM_TOKEN_VALIDITY, roles);
    }

    public String createRefreshToken(String username) {
        return createToken(username, AuthenticationClaim.REFRESH_TOKEN, REFRESH_TOKEN_VALIDITY, null);
    }

    private String createToken(String username, AuthenticationClaim type, long validity, List<UserRole> roles) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + validity);
        Key privateKey = keyProvider.getPrivateKey();

        JwtBuilder builder = Jwts.builder()
                .subject(username)
                .claim("type", type)
                .issuedAt(now)
                .expiration(expiration);

        if (roles != null) {
            builder.claim("auth", roles.stream().map(UserRole::name).toList());
        }

        return builder.signWith(privateKey).compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaimsFromToken(token);
        AuthenticationClaim tokenType = claims.get("type", AuthenticationClaim.class);

        if (!tokenType.equals(AuthenticationClaim.ACCESS_TOKEN)) {
            throw new JwtException("Invalid token type");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getClaimsFromToken(String token) {
        PublicKey publicKey = keyProvider.getPublicKey();
        try {
            return Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Token not valid", e);
        }
    }
}
