package com.aziz.order.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record OrderItemDto(Long variantId, Long productId, String productName, String sku,
                           Map<String, String> options, int quantity,
                           BigDecimal unitPrice, BigDecimal lineTotal) {}
