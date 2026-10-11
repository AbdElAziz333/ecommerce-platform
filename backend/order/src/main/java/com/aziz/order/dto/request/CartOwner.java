package com.aziz.order.dto.request;

public record CartOwner(Long userId, String guestId) {
    public boolean isUser() {
        return userId != null;
    }
}