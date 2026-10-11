package com.aziz.order.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record VariantInfo(Long variantId, Long productId, String productName, String slug,
                          String sku, BigDecimal price, int stockQuantity, Map<String, String> options) {}
