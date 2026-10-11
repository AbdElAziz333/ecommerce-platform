package com.aziz.order.model;

import com.aziz.order.dto.request.ShippingAddress;
import com.aziz.order.util.enums.*;
import com.aziz.order.util.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private Long version;   // concurrent status changes fail with a 409 instead of overwriting each other

    @Column(nullable = false, updatable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false, updatable = false)
    private Long userId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "streetLine", column = @Column(name = "shipping_street_line", nullable = false, updatable = false)),
            @AttributeOverride(name = "city", column = @Column(name = "shipping_city", nullable = false, updatable = false)),
            @AttributeOverride(name = "state", column = @Column(name = "shipping_state", nullable = false, updatable = false)),
            @AttributeOverride(name = "postalCode", column = @Column(name = "shipping_postal_code", nullable = false, updatable = false))
    })
    private ShippingAddress shippingAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false, updatable = false)
    private BigDecimal subtotal;

    @Column(nullable = false, updatable = false)
    private BigDecimal shippingAmount;

    @Column(nullable = false, updatable = false)
    private BigDecimal totalAmount;

    @Column(updatable = false, length = 1000)
    private String notes;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    /** The only way to create an order; totals are always computed here. */
    public static Order place(Long userId, ShippingAddress address, PaymentMethod method,
                              BigDecimal shippingAmount, String notes, List<OrderItem> items) {
        Order o = new Order();
        o.userId = userId;
        o.shippingAddress = address;
        o.paymentMethod = method;
        o.notes = notes;
        o.status = OrderStatus.PENDING;
        o.paymentStatus = PaymentStatus.PENDING;
        o.orderNumber = "ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();

        items.forEach(item -> {
            item.attachTo(o);
            o.items.add(item);
        });
        o.subtotal = o.items.stream().map(OrderItem::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        o.shippingAmount = shippingAmount;
        o.totalAmount = o.subtotal.add(shippingAmount);
        return o;
    }

    public void changeStatus(OrderStatus next) {
        if (!status.canMoveTo(next)) {
            throw new BadRequestException("Cannot change order status from " + status + " to " + next);
        }
        status = next;
    }
}