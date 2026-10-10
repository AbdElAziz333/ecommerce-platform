package com.aziz.product.service;

import com.aziz.product.dto.request.CreateProductRequest;
import com.aziz.product.dto.request.UpdateVariantRequest;
import com.aziz.product.dto.request.VariantRequest;
import com.aziz.product.dto.response.ProductDto;
import com.aziz.product.dto.request.UpdateProductRequest;
import com.aziz.product.mapper.ProductMapper;
import com.aziz.product.model.Product;
import com.aziz.product.model.ProductVariant;
import com.aziz.product.repository.ProductRepository;
import com.aziz.product.repository.ProductVariantRepository;
import com.aziz.product.util.Slugs;
import com.aziz.product.util.exceptions.BadRequestException;
import com.aziz.product.util.exceptions.DuplicateProductException;
import com.aziz.product.util.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private static final int PAGE_SIZE = 100;

    private final ProductRepository repository;
    private final ProductVariantRepository variantRepository;
    private final ProductMapper mapper;

    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(int page) {
        return repository.findAll(PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt", "id")))
                .map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public ProductDto getProductBySlug(String slug) {
        return repository.findBySlug(slug)
                .map(mapper::toDto)
                .orElseThrow(() -> new NotFoundException("Product not found: " + slug));
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByOwner(Long userId) {
        return repository.findAllByUserIdOrderByCreatedAtDesc(userId).stream().map(mapper::toDto).toList();
    }

    @Transactional
    public ProductDto createProduct(Long userId, CreateProductRequest r) {
        validateCombinations(r.variants().stream().map(VariantRequest::optionValues).toList());
        validateNewSkus(r.variants().stream().map(VariantRequest::sku).toList());

        Product product = repository.save(mapper.toEntity(userId, Slugs.from(r.name()), r));

        log.info("Product {} created by user {}", product.getId(), userId);
        return mapper.toDto(product);
    }

    @Transactional
    public ProductDto updateProduct(Long userId, Long id, UpdateProductRequest r) {
        Product product = findOwned(userId, id);
        product.update(r.name(), r.description(), r.shortDescription());
        return mapper.toDto(product);
    }

    @Transactional
    public void deleteProduct(Long userId, Long id) {
        repository.delete(findOwned(userId, id));
        log.info("Product {} deleted by user {}", id, userId);
    }

    @Transactional
    public ProductDto addVariant(Long userId, Long productId, VariantRequest r) {
        Product product = findOwned(userId, productId);

        List<Map<String, String>> combinations = combinationsOf(product, null);
        combinations.add(r.optionValues());
        validateCombinations(combinations);
        validateNewSkus(List.of(r.sku()));

        product.addVariant(mapper.toVariant(r));
        return mapper.toDto(product);
    }

    @Transactional
    public ProductDto updateVariant(Long userId, Long productId, Long variantId, UpdateVariantRequest r) {
        Product product = findOwned(userId, productId);
        ProductVariant variant = findVariant(product, variantId);

        if (r.sku() != null && variantRepository.existsBySkuAndIdNot(r.sku(), variantId)) {
            throw new DuplicateProductException("SKU already exists: " + r.sku());
        }
        if (r.optionValues() != null) {
            List<Map<String, String>> combinations = combinationsOf(product, variantId);
            combinations.add(r.optionValues());
            validateCombinations(combinations);
        }

        variant.update(r.sku(), r.price(), r.stockQuantity(), r.optionValues());
        return mapper.toDto(product);
    }

    @Transactional
    public void deleteVariant(Long userId, Long productId, Long variantId) {
        Product product = findOwned(userId, productId);
        ProductVariant variant = findVariant(product, variantId);

        if (product.getVariants().size() == 1) {
            throw new BadRequestException("A product must keep at least one variant");
        }
        product.removeVariant(variant);
    }

    // 404 for both "doesn't exist" and "not yours"
    private Product findOwned(Long userId, Long id) {
        return repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
    }

    private ProductVariant findVariant(Product product, Long variantId) {
        return product.getVariants().stream()
                .filter(v -> v.getId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Variant not found: " + variantId));
    }

    // mutable copy of the product's current combinations, optionally skipping one variant
    private List<Map<String, String>> combinationsOf(Product product, Long excludedVariantId) {
        return product.getVariants().stream()
                .filter(v -> !v.getId().equals(excludedVariantId))
                .map(ProductVariant::getOptionValues)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // every variant must use the same option names, and no two may share a combination
    private void validateCombinations(List<Map<String, String>> combinations) {
        Set<String> keys = combinations.get(0).keySet();
        Set<Map<String, String>> seen = new HashSet<>();

        for (Map<String, String> values : combinations) {
            if (!values.keySet().equals(keys)) {
                throw new BadRequestException("All variants must use the same options: " + keys);
            }
            if (!seen.add(values)) {
                throw new BadRequestException("Duplicate variant combination: " + values);
            }
        }
    }

    // friendly error up front; the UNIQUE constraint is the safety net for races
    private void validateNewSkus(List<String> skus) {
        if (new HashSet<>(skus).size() != skus.size()) {
            throw new DuplicateProductException("Duplicate SKU in request");
        }
        List<String> taken = variantRepository.findExistingSkus(skus);
        if (!taken.isEmpty()) {
            throw new DuplicateProductException("SKU already exists: " + taken.get(0));
        }
    }
}