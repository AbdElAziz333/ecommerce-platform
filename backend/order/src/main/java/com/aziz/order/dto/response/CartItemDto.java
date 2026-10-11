package com.aziz.order.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record CartItemDto(Long variantId, Long productId, String name, String slug, String sku,
                          Map<String, String> options, int quantity, BigDecimal unitPrice,
                          BigDecimal lineTotal, boolean inStock) {}