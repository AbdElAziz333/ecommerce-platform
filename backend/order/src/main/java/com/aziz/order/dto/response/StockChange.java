package com.aziz.order.dto.response;

public record StockChange(Long variantId, int quantity) {
    public StockChange {
        if (variantId == null || quantity < 1) throw new IllegalArgumentException("Invalid stock change");
    }
}