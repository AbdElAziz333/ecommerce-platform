package com.aziz.order.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateOrderItemRequest(
        @NotNull(message = "Product name is required")
        String productNameSnapshot,

        @NotNull(message = "SKU is required")
        String skuSnapshot,

        @Min(1)
        @NotNull(message = "Quantity is required")
        Integer quantity,

        @DecimalMin(value = "0.01")
        @NotNull(message = "Unit price is required")
        BigDecimal unitPrice
) {}