package com.aziz.gateway.security;

import com.aziz.gateway.config.SessionProperties;
import com.aziz.gateway.dto.request.TrustedHeadersRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
public class GatewayHeadersFilter extends OncePerRequestFilter {
    public static final String USER_ID = "User-Id";
    public static final String USER_ROLE = "User-Role";
    public static final String SESSION_ID = "Session-Id";
    private static final Set<String> MANAGED = Set.of(USER_ID, USER_ROLE, SESSION_ID);

    private final SessionProperties session;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        Map<String, String> trusted = new HashMap<>();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);

        if (authenticated) {
            trusted.put(USER_ID, String.valueOf(auth.getPrincipal()));
            auth.getAuthorities().stream().findFirst()
                    .ifPresent(a -> trusted.put(USER_ROLE, a.getAuthority()));
        }

        // guests get a session id cookie; logged-in users keep theirs (e.g. for cart merging)
        String sessionId = readSessionId(request);
        if (sessionId == null && !authenticated) {
            sessionId = UUID.randomUUID().toString();
            response.addHeader(HttpHeaders.SET_COOKIE, sessionCookie(sessionId).toString());
        }

        if (sessionId != null) {
            trusted.put(SESSION_ID, sessionId);
        }

        // client-supplied copies of the managed headers are dropped; only trusted values pass
        chain.doFilter(new TrustedHeadersRequest(request, MANAGED, trusted), response);
    }

    private String readSessionId(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, session.cookie());
        if (cookie == null) {
            return null;
        }

        try {
            return UUID.fromString(cookie.getValue()).toString();   // reject arbitrary values
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private ResponseCookie sessionCookie(String id) {
        return ResponseCookie.from(session.cookie(), id)
                .httpOnly(true)
                .secure(session.secure())
                .sameSite("Lax")
                .path("/")
                .maxAge(session.maxAge())
                .build();
    }
}