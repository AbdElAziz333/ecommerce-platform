package com.aziz.product.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record VariantDto(
        Long id,
        String sku,
        BigDecimal price,
        Integer stockQuantity,
        Map<String, String> optionValues
) {}