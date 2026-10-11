package com.aziz.order.util.enums;

public enum OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED;

    public boolean canMoveTo(OrderStatus next) {
        return switch (this) {
            case PENDING -> next == CONFIRMED || next == CANCELLED;
            case CONFIRMED -> next == SHIPPED || next == CANCELLED;
            case SHIPPED -> next == DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}