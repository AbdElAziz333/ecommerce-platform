package com.aziz.order.controller;

import com.aziz.order.dto.request.AddItemRequest;
import com.aziz.order.dto.request.CartOwner;
import com.aziz.order.dto.response.CartDto;
import com.aziz.order.dto.request.UpdateQuantityRequest;
import com.aziz.order.service.CartService;
import com.aziz.order.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService service;

    @GetMapping
    public ApiResponse<CartDto> get(CartOwner owner) {
        return ApiResponse.ok(service.getCart(owner));
    }

    @GetMapping("/count")
    public ApiResponse<Integer> count(CartOwner owner) {
        return ApiResponse.ok(service.getItemCount(owner));
    }

    @PostMapping("/items")
    public ApiResponse<CartDto> add(CartOwner owner, @RequestBody @Valid AddItemRequest request) {
        return ApiResponse.ok(service.addItem(owner, request));
    }

    @PatchMapping("/items/{variantId}")
    public ApiResponse<CartDto> updateQuantity(CartOwner owner, @PathVariable Long variantId, @RequestBody @Valid UpdateQuantityRequest request) {
        return ApiResponse.ok(service.updateQuantity(owner, variantId, request.quantity()));
    }

    @DeleteMapping("/items/{variantId}")
    public ApiResponse<CartDto> remove(CartOwner owner, @PathVariable Long variantId) {
        return ApiResponse.ok(service.removeItem(owner, variantId));
    }

    // TODO: should be called after login
    @PostMapping("/merge")
    public ApiResponse<CartDto> merge(CartOwner owner) {
        return ApiResponse.ok(service.mergeGuestCart(owner));
    }
}