package com.example.todolist.common.security;

import com.example.todolist.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private static final int MIN_SECRET_KEY_BYTES = 32;

    private final SecretKey secretKey;
    private final long tokenExpirationHours;

    public JwtTokenProvider(
            @Value("${jwt.secret:change-this-secret-key-to-a-secure-value-1234567890}") String jwtSecret,
            @Value("${jwt.expiration-hours:24}") long tokenExpirationHours
    ) {
        byte[] secretBytes = jwtSecret.trim().getBytes(StandardCharsets.UTF_8);
        validateSecretKey(secretBytes);

        this.secretKey = Keys.hmacShaKeyFor(secretBytes);
        this.tokenExpirationHours = tokenExpirationHours;
    }

    private void validateSecretKey(byte[] secretBytes) {
        if (secretBytes.length < MIN_SECRET_KEY_BYTES) {
            throw new IllegalStateException(
                    "JWT secret must be at least 32 bytes for HS256. " +
                    "Update the JWT_SECRET environment variable with a longer secret."
            );
        }
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(tokenExpirationHours, ChronoUnit.HOURS);

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public Claims parseClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extractUserId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }
}
