package com.aziz.gateway.service;

import com.aziz.gateway.dto.request.CreateUserRequest;
import com.aziz.gateway.dto.response.AuthTokens;
import com.aziz.gateway.model.User;
import com.aziz.gateway.repository.RefreshTokenRepository;
import com.aziz.gateway.repository.UserRepository;
import com.aziz.gateway.dto.request.LoginRequest;
import com.aziz.gateway.util.exceptions.AlreadyExistsException;
import com.aziz.gateway.util.exceptions.InvalidCredentialsException;
import com.aziz.gateway.util.exceptions.UnauthorizedException;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class AuthService {
    private static final String DEFAULT_ROLE = "ROLE_USER";
    private static final String DEFAULT_LANGUAGE = "ARABIC";

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokens;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final String dummyHash;

    public AuthService(JwtService jwtService, RefreshTokenRepository refreshTokens,
                       UserRepository users, PasswordEncoder encoder) {
        this.jwtService = jwtService;
        this.refreshTokens = refreshTokens;
        this.users = users;
        this.encoder = encoder;
        // unknown emails still pay for one BCrypt check, so response time doesn't reveal who is registered
        this.dummyHash = encoder.encode("dummy-password");
    }

    public AuthTokens login(LoginRequest request) {
        User user = users.findByEmail(normalize(request.email())).orElse(null);

        boolean matches = encoder.matches(request.password(), user != null ? user.getPassword() : dummyHash);
        if (user == null || !matches) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        log.info("User logged in: {}", user.getId());
        return issueTokens(user);
    }

    public AuthTokens register(CreateUserRequest request) {
        String email = normalize(request.email());
        if (users.existsByEmail(email)) {
            throw new AlreadyExistsException("User already exists with provided email");
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(email)
                .password(encoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .role(DEFAULT_ROLE)
                .preferredLanguage(DEFAULT_LANGUAGE)
                .build();

        try {
            users.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {   // concurrent registration with the same email
            throw new AlreadyExistsException("User already exists with provided email");
        }

        log.info("User registered: {}", user.getId());
        return issueTokens(user);
    }

    public AuthTokens refreshToken(String refreshToken) {
        Claims claims = jwtService.parseRefreshToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Refresh token is invalid or expired"));
        Long userId = Long.valueOf(claims.getSubject());

        // atomic: a refresh token can be used exactly once, even with concurrent requests
        boolean valid = refreshTokens.consume(claims.getId()).filter(userId::equals).isPresent();
        if (!valid) {
            throw new UnauthorizedException("Refresh token has been revoked or already used");
        }

        User user = users.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User no longer exists"));
        return issueTokens(user);
    }

    public void logout(String refreshToken) {
        jwtService.parseRefreshToken(refreshToken).ifPresent(c -> refreshTokens.consume(c.getId()));
    }

    private AuthTokens issueTokens(User user) {
        String jti = UUID.randomUUID().toString();
        String refreshToken = jwtService.generateRefreshToken(user.getId(), jti);
        refreshTokens.save(user.getId(), jti);
        return new AuthTokens(jwtService.generateAccessToken(user.getId(), user.getRole()), refreshToken);
    }

    private static String normalize(String email) {
        return email.trim().toLowerCase();
    }
}