package com.aziz.order.dto.request;

import com.aziz.order.util.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(
        @NotNull @Valid ShippingAddress shippingAddress,
        @NotNull PaymentMethod paymentMethod,
        @Size(max = 1000) String notes
) {}