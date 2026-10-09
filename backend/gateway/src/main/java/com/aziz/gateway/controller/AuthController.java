package com.aziz.gateway.controller;

import com.aziz.gateway.dto.request.CreateUserRequest;
import com.aziz.gateway.dto.request.LoginRequest;
import com.aziz.gateway.dto.response.AuthTokens;
import com.aziz.gateway.service.AuthCookieService;
import com.aziz.gateway.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;
    private final AuthCookieService cookieService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest request) {
        AuthTokens tokens = service.login(request);

        return ResponseEntity.ok()
                .headers(cookieService.authHeaders(tokens))
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid CreateUserRequest request) {
        AuthTokens tokens = service.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(cookieService.authHeaders(tokens))
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(HttpServletRequest request) {
        AuthTokens tokens = service.refreshToken(cookieService.readRefreshToken(request));

        return ResponseEntity.ok()
                .headers(cookieService.authHeaders(tokens))
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        service.logout(cookieService.readRefreshToken(request));

        return ResponseEntity.ok().headers(cookieService.clearHeaders()).build();
    }
}