package com.aziz.gateway.service;

import com.aziz.gateway.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {
    private static final String TYPE = "type";
    private static final String ACCESS = "access";
    private static final String REFRESH = "refresh";

    private final JwtProperties props;
    private final SecretKey key;
    private final JwtParser parser;

    public JwtService(JwtProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
        this.parser = Jwts.parser().verifyWith(key).build();
    }

    public String generateAccessToken(Long userId, String role) {
        return builder(userId, ACCESS, props.accessToken().ttl())
                .claim("role", role)
                .signWith(key).compact();
    }

    public String generateRefreshToken(Long userId, String jti) {
        return builder(userId, REFRESH, props.refreshToken().ttl())
                .id(jti)
                .signWith(key).compact();
    }

    /** Claims if the token is a valid, unexpired access token; otherwise empty. */
    public Optional<Claims> parseAccessToken(String token) {
        return parse(token, ACCESS);
    }

    public Optional<Claims> parseRefreshToken(String token) {
        return parse(token, REFRESH);
    }

    private JwtBuilder builder(Long userId, String type, Duration ttl) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .claim(TYPE, type)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttl)));
    }

    private Optional<Claims> parse(String token, String expectedType) {
        if (token == null || token.isBlank()) return Optional.empty();
        try {
            Claims claims = parser.parseSignedClaims(token).getPayload();
            return expectedType.equals(claims.get(TYPE, String.class)) ? Optional.of(claims) : Optional.empty();
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}