package com.aziz.product.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record CreateProductRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 10_000) String description,
        @NotBlank @Size(max = 1_000) String shortDescription,
        @NotEmpty @Size(max = 100) @Valid List<VariantRequest> variants
) {}