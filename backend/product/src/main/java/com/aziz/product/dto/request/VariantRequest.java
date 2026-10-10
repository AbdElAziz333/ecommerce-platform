package com.aziz.product.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Map;

public record VariantRequest(
        @NotBlank @Size(max = 255) String sku,
        @NotNull @Positive @Digits(integer = 10, fraction = 2) BigDecimal price,
        @NotNull @PositiveOrZero Integer stockQuantity,
        @NotNull @Size(max = 10) Map<@NotBlank @Size(max = 50) String, @NotBlank @Size(max = 100) String> optionValues
) {}