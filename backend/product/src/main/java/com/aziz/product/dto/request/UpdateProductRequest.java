package com.aziz.product.dto.request;

import com.aziz.product.util.ValidationRules;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// null = "not provided"; if provided, it must not be blank
public record UpdateProductRequest(
        @Pattern(regexp = ValidationRules.NOT_BLANK_REGEX) @Size(max = 255) String name,
        @Pattern(regexp = ValidationRules.NOT_BLANK_REGEX) @Size(max = 10_000) String description,
        @Pattern(regexp = ValidationRules.NOT_BLANK_REGEX) @Size(max = 1_000) String shortDescription
) {}