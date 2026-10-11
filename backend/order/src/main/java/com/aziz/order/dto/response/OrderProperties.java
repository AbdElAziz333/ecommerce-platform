package com.aziz.order.dto.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Validated
@ConfigurationProperties("orders")
public record OrderProperties(@NotNull @PositiveOrZero BigDecimal shippingFee) {}