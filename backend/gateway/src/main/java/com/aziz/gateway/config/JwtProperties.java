package com.aziz.gateway.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties("jwt")
public record JwtProperties(
        @NotBlank @Size(min = 32) String secret,
        @Valid @NotNull Token accessToken,
        @Valid @NotNull Token refreshToken
) {
    public record Token(@NotBlank String cookieName, @NotNull Duration ttl, boolean secured) {}
}