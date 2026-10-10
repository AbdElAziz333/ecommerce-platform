package com.aziz.product.dto.request;

import com.aziz.product.util.ValidationRules;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Map;

public record UpdateVariantRequest(
        @Pattern(regexp = ValidationRules.NOT_BLANK_REGEX) @Size(max = 255) String sku,
        @Positive @Digits(integer = 10, fraction = 2) BigDecimal price,
        @PositiveOrZero Integer stockQuantity,
        @Size(max = 10) Map<@NotBlank @Size(max = 50) String, @NotBlank @Size(max = 100) String> optionValues
) {}