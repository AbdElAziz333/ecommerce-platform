package com.aziz.gateway.service;

import com.aziz.gateway.config.JwtProperties;
import com.aziz.gateway.dto.response.AuthTokens;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthCookieService {
    private final JwtProperties jwt;

    public HttpHeaders authHeaders(AuthTokens tokens) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessCookie(tokens.accessToken(), jwt.accessToken().ttl()).toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie(tokens.refreshToken(), jwt.refreshToken().ttl()).toString());
        return headers;
    }

    public HttpHeaders clearHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessCookie("", Duration.ZERO).toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie("", Duration.ZERO).toString());
        return headers;
    }

    public String readAccessToken(HttpServletRequest request) {
        return read(request, jwt.accessToken().cookieName());
    }

    public String readRefreshToken(HttpServletRequest request) {
        return read(request, jwt.refreshToken().cookieName());
    }

    private ResponseCookie accessCookie(String value, Duration maxAge) {
        return cookie(jwt.accessToken(), value, maxAge, "Lax", "/");
    }

    private ResponseCookie refreshCookie(String value, Duration maxAge) {
        return cookie(jwt.refreshToken(), value, maxAge, "Strict", "/api/v1/auth");
    }

    private static ResponseCookie cookie(JwtProperties.Token t, String value, Duration maxAge,
                                         String sameSite, String path) {
        return ResponseCookie.from(t.cookieName(), value)
                .httpOnly(true).secure(t.secured())
                .sameSite(sameSite).path(path).maxAge(maxAge)
                .build();
    }

    private static String read(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        return cookie == null ? null : cookie.getValue();
    }
}