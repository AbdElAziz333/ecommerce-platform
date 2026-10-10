package com.aziz.product.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;   // guards concurrent stock updates

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    private Product product;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "option_values", columnDefinition = "jsonb", nullable = false)
    private Map<String, String> optionValues = new HashMap<>();

    public static ProductVariant create(String sku, BigDecimal price, Integer stockQuantity,
                                        Map<String, String> optionValues) {
        ProductVariant v = new ProductVariant();
        v.sku = sku;
        v.price = price;
        v.stockQuantity = stockQuantity;
        v.optionValues = new HashMap<>(optionValues);
        return v;
    }

    // null means "leave unchanged"
    public void update(String sku, BigDecimal price, Integer stockQuantity, Map<String, String> optionValues) {
        if (sku != null) this.sku = sku;
        if (price != null) this.price = price;
        if (stockQuantity != null) this.stockQuantity = stockQuantity;
        if (optionValues != null) this.optionValues = new HashMap<>(optionValues);
    }

    void attachTo(Product product) {
        this.product = product;
    }
}