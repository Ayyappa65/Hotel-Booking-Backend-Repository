package com.ayyappa.hotelbooking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility class for generating and validating JWT tokens.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration.ms:86400000}") // Default 1 day
    private long expirationTime;

    private SecretKey signingKey;
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build();
    }

    /**
     * Generate a JWT token with subject (email), role, iat and exp.
     *
     * @param email the user email (used as subject)
     * @param role the user role
     * @return JWT token as a String
     */
    public String generateToken(String email, String role) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + expirationTime);

    return Jwts.builder()
            .setSubject(email)
            .claim("role", role)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(signingKey)
            .compact();
    }

    /**
     * Validate and parse JWT token.
     *
     * @param token the JWT token
     * @return Claims from the token
     */
    public Claims validateToken(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    /**
    * Extract role from JWT token.
    *
    * @param token the JWT token
    * @return role as a string (e.g., "ROLE_ADMIN")
    */
    public String extractRole(String token) {
        Claims claims = validateToken(token);
        String role = claims.get("role", String.class);
        return role != null ? "ROLE_" + role.toUpperCase() : null;
    }

}
