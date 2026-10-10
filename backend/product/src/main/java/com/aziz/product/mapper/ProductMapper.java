package com.aziz.product.mapper;

import com.aziz.product.dto.request.CreateProductRequest;
import com.aziz.product.dto.request.VariantRequest;
import com.aziz.product.dto.response.ProductDto;
import com.aziz.product.dto.response.VariantDto;
import com.aziz.product.model.Product;
import com.aziz.product.model.ProductVariant;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class ProductMapper {

    // variants are created through the aggregate root
    public Product toEntity(Long userId, String slug, CreateProductRequest r) {
        Product product = Product.create(userId, slug, r.name(), r.description(), r.shortDescription());
        r.variants().forEach(v -> product.addVariant(toVariant(v)));
        return product;
    }

    public ProductVariant toVariant(VariantRequest r) {
        return ProductVariant.create(r.sku(), r.price(), r.stockQuantity(), r.optionValues());
    }

    // must be called inside a transaction: it reads the lazy variants collection
    public ProductDto toDto(Product p) {
        List<VariantDto> variants = p.getVariants().stream().map(this::toVariantDto).toList();
        BigDecimal priceFrom = variants.stream().map(VariantDto::price).min(Comparator.naturalOrder()).orElse(null);

        return new ProductDto(p.getId(), p.getName(), p.getDescription(), p.getShortDescription(),
                p.getSlug(), priceFrom, variants, p.getCreatedAt());
    }

    public VariantDto toVariantDto(ProductVariant v) {
        return new VariantDto(v.getId(), v.getSku(), v.getPrice(), v.getStockQuantity(),
                Map.copyOf(v.getOptionValues()));
    }
}