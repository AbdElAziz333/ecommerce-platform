package com.aziz.gateway.security;

import com.aziz.gateway.service.AuthCookieService;
import com.aziz.gateway.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AuthCookieService cookies;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        jwtService.parseAccessToken(cookies.readAccessToken(request)).ifPresent(claims -> {
            Long userId = Long.valueOf(claims.getSubject());
            String role = claims.get("role", String.class);
            SecurityContextHolder.getContext().setAuthentication(
                    UsernamePasswordAuthenticationToken.authenticated(
                            userId, null, List.of(new SimpleGrantedAuthority(role))));
        });

        chain.doFilter(request, response);
    }
}