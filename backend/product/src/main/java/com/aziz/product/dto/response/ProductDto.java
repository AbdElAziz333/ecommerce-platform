package com.aziz.product.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ProductDto(
        Long id,
        String name,
        String description,
        String shortDescription,
        String slug,
        BigDecimal priceFrom,
        List<VariantDto> variants,
        Instant createdAt
) {}