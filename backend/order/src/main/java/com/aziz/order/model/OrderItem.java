package com.aziz.order.model;

import com.aziz.order.dto.response.VariantInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    private Order order;

    // everything below is a snapshot taken at purchase time and never changes
    @Column(nullable = false, updatable = false)
    private Long variantId;

    @Column(nullable = false, updatable = false)
    private Long productId;

    @Column(nullable = false, updatable = false)
    private String productName;

    @Column(nullable = false, updatable = false)
    private String sku;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false, updatable = false)
    private Map<String, String> options;

    @Column(nullable = false, updatable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false, updatable = false)
    private int quantity;

    public static OrderItem snapshot(VariantInfo v, int quantity) {
        OrderItem i = new OrderItem();
        i.variantId = v.variantId();
        i.productId = v.productId();
        i.productName = v.productName();
        i.sku = v.sku();
        i.options = new HashMap<>(v.options());
        i.unitPrice = v.price();
        i.quantity = quantity;
        return i;
    }

    public BigDecimal lineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    void attachTo(Order order) {
        this.order = order;
    }
}