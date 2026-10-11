package com.aziz.order.mapper;

import com.aziz.order.dto.request.CreateOrderRequest;
import com.aziz.order.dto.response.OrderDto;
import com.aziz.order.dto.request.CreateOrderItemRequest;
import com.aziz.order.dto.response.OrderItemDto;
import com.aziz.order.dto.response.OrderSummaryDto;
import com.aziz.order.model.Order;
import com.aziz.order.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class OrderMapper {
    public OrderSummaryDto toSummary(Order o) {
        return new OrderSummaryDto(o.getId(), o.getOrderNumber(), o.getStatus(), o.getPaymentStatus(),
                o.getTotalAmount(), o.getCreatedAt());
    }

    // items must already be loaded (the repository methods use an entity graph)
    public OrderDto toDto(Order o) {
        return new OrderDto(o.getId(), o.getOrderNumber(), o.getStatus(), o.getPaymentStatus(),
                o.getPaymentMethod(), o.getShippingAddress(), o.getSubtotal(), o.getShippingAmount(),
                o.getTotalAmount(), o.getNotes(), o.getCreatedAt(),
                o.getItems().stream().map(this::toItemDto).toList());
    }

    private OrderItemDto toItemDto(OrderItem i) {
        return new OrderItemDto(i.getVariantId(), i.getProductId(), i.getProductName(), i.getSku(),
                Map.copyOf(i.getOptions()), i.getQuantity(), i.getUnitPrice(), i.lineTotal());
    }
}