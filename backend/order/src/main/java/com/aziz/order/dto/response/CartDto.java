package com.aziz.order.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartDto(List<CartItemDto> items, int itemCount, BigDecimal subtotal) {
    public static final CartDto EMPTY = new CartDto(List.of(), 0, BigDecimal.ZERO);
}