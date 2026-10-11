package com.aziz.order.dto.response;

import com.aziz.order.util.enums.OrderStatus;
import com.aziz.order.util.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderSummaryDto(UUID id, String orderNumber, OrderStatus status, PaymentStatus paymentStatus,
                              BigDecimal totalAmount, Instant createdAt) {}
