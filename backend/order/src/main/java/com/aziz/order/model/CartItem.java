package com.aziz.order.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false, updatable = false)
    private Cart cart;

    @Column(nullable = false, updatable = false)
    private Long variantId;

    @Column(nullable = false)
    private int quantity;

    CartItem(Cart cart, Long variantId, int quantity) {
        this.cart = cart;
        this.variantId = variantId;
        this.quantity = quantity;
    }

    void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}