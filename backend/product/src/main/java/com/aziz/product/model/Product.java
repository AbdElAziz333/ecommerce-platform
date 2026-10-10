package com.aziz.product.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "short_description", length = 1000)
    private String shortDescription;

    @Column(nullable = false, unique = true, updatable = false)
    private String slug;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)   // avoids N+1 when listing products without the entity graph
    private List<ProductVariant> variants = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static Product create(Long userId, String slug, String name,
                                 String description, String shortDescription) {
        Product p = new Product();
        p.userId = userId;
        p.slug = slug;
        p.name = name;
        p.description = description;
        p.shortDescription = shortDescription;
        return p;
    }

    // null means "leave unchanged"
    public void update(String name, String description, String shortDescription) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (shortDescription != null) this.shortDescription = shortDescription;
    }

    public void addVariant(ProductVariant variant) {
        variants.add(variant);
        variant.attachTo(this);
    }

    public void removeVariant(ProductVariant variant) {
        variants.remove(variant);   // orphanRemoval deletes it
    }
}