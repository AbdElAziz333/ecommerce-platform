package com.aziz.order.dto.response;

import com.aziz.order.dto.request.ShippingAddress;
import com.aziz.order.util.enums.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderDto(UUID id, String orderNumber, OrderStatus status, PaymentStatus paymentStatus,
                       PaymentMethod paymentMethod, ShippingAddress shippingAddress,
                       BigDecimal subtotal, BigDecimal shippingAmount, BigDecimal totalAmount,
                       String notes, Instant createdAt, List<OrderItemDto> items) {}
