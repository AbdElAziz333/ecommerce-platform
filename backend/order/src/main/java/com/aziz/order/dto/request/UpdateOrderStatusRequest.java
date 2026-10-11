package com.aziz.order.dto.request;

import com.aziz.order.util.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull OrderStatus status) {}
