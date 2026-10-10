package com.aziz.product.controller;

import com.aziz.product.dto.request.CreateProductRequest;
import com.aziz.product.dto.request.UpdateVariantRequest;
import com.aziz.product.dto.request.VariantRequest;
import com.aziz.product.dto.response.ProductDto;
import com.aziz.product.dto.request.UpdateProductRequest;
import com.aziz.product.service.ProductService;
import com.aziz.product.util.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @GetMapping
    public ApiResponse<Page<ProductDto>> getProducts(@RequestParam(defaultValue = "0") @Min(0) int page) {
        return ApiResponse.ok(service.getProducts(page));
    }

    @GetMapping("/{slug}")
    public ApiResponse<ProductDto> getProductBySlug(@PathVariable String slug) {
        return ApiResponse.ok(service.getProductBySlug(slug));
    }

    // ---- admin ----

    @GetMapping("/mine")
    public ApiResponse<List<ProductDto>> getMyProducts(@RequestHeader("User-Id") Long userId) {
        return ApiResponse.ok(service.getProductsByOwner(userId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductDto> createProduct(@RequestHeader("User-Id") Long userId,
                                                 @RequestBody @Valid CreateProductRequest request) {
        return ApiResponse.ok(service.createProduct(userId, request));
    }

    @PatchMapping("/{id}")
    public ApiResponse<ProductDto> updateProduct(@RequestHeader("User-Id") Long userId,
                                                 @PathVariable Long id,
                                                 @RequestBody @Valid UpdateProductRequest request) {
        return ApiResponse.ok(service.updateProduct(userId, id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@RequestHeader("User-Id") Long userId, @PathVariable Long id) {
        service.deleteProduct(userId, id);
    }

    @PostMapping("/{id}/variants")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductDto> addVariant(@RequestHeader("User-Id") Long userId,
                                              @PathVariable Long id,
                                              @RequestBody @Valid VariantRequest request) {
        return ApiResponse.ok(service.addVariant(userId, id, request));
    }

    @PatchMapping("/{id}/variants/{variantId}")
    public ApiResponse<ProductDto> updateVariant(@RequestHeader("User-Id") Long userId,
                                                 @PathVariable Long id,
                                                 @PathVariable Long variantId,
                                                 @RequestBody @Valid UpdateVariantRequest request) {
        return ApiResponse.ok(service.updateVariant(userId, id, variantId, request));
    }

    @DeleteMapping("/{id}/variants/{variantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVariant(@RequestHeader("User-Id") Long userId,
                              @PathVariable Long id, @PathVariable Long variantId) {
        service.deleteVariant(userId, id, variantId);
    }
}