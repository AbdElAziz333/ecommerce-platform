package com.aziz.order.model;

import com.aziz.order.util.exception.NotFoundException;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Cart {
    public static final int MAX_QUANTITY = 99;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;      // exactly one of userId / guestId is set (CHECK constraint in the DB)
    private String guestId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // maintained by touch(): changing the inverse `items` collection doesn't dirty the cart,
    // so @LastModifiedDate would not fire, and the stale-cart cleanup relies on this value
    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    public static Cart forUser(Long userId) {
        Cart c = new Cart();
        c.userId = userId;
        return c;
    }

    public static Cart forGuest(String guestId) {
        Cart c = new Cart();
        c.guestId = guestId;
        return c;
    }

    public void addItem(Long variantId, int quantity) {
        find(variantId).ifPresentOrElse(
                item -> item.setQuantity(cap(item.getQuantity() + quantity)),
                () -> items.add(new CartItem(this, variantId, cap(quantity))));
        touch();
    }

    /** quantity <= 0 removes the item */
    public void setQuantity(Long variantId, int quantity) {
        if (quantity <= 0) {
            removeItem(variantId);
            return;
        }
        find(variantId).orElseThrow(() -> new NotFoundException("Item not in cart"))
                .setQuantity(cap(quantity));
        touch();
    }

    public void removeItem(Long variantId) {
        items.removeIf(i -> i.getVariantId().equals(variantId));
        touch();
    }

    public void clear() {
        items.clear();
        touch();
    }

    public void mergeFrom(Cart other) {
        other.items.forEach(i -> addItem(i.getVariantId(), i.getQuantity()));
    }

    public void claimFor(Long userId) {
        this.userId = userId;
        this.guestId = null;
        touch();
    }

    public int itemCount() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    private Optional<CartItem> find(Long variantId) {
        return items.stream().filter(i -> i.getVariantId().equals(variantId)).findFirst();
    }

    private static int cap(int quantity) { return Math.min(quantity, MAX_QUANTITY); }
    private void touch() { updatedAt = Instant.now(); }
}