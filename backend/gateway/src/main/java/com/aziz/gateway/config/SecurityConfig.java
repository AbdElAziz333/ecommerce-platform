package com.aziz.gateway.config;

import com.aziz.gateway.security.GatewayHeadersFilter;
import com.aziz.gateway.security.JwtAuthenticationFilter;
import com.aziz.gateway.service.AuthCookieService;
import com.aziz.gateway.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, SessionProperties.class})
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtService jwtService;
    private final AuthCookieService cookieService;
    private final SessionProperties sessionProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(cookieService, jwtService);
        GatewayHeadersFilter headersFilter = new GatewayHeadersFilter(sessionProperties);

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**", "/actuator/health/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                        .requestMatchers("/api/v1/carts/**").permitAll()   // guests use Session-Id
                        .requestMatchers("/api/v1/users/current/**",
                                "/api/v1/addresses/**",
                                "/api/v1/orders/**",
                                "/api/v1/order-items/**").authenticated()
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                        .anyRequest().denyAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(headersFilter, JwtAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}