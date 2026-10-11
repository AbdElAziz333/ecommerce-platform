package com.aziz.order.controller;

import com.aziz.order.dto.request.CreateOrderRequest;
import com.aziz.order.dto.request.UpdateOrderStatusRequest;
import com.aziz.order.dto.response.OrderDto;
import com.aziz.order.dto.response.OrderSummaryDto;
import com.aziz.order.service.OrderService;
import com.aziz.order.util.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    @GetMapping
    public ApiResponse<Page<OrderSummaryDto>> getOrders(@RequestHeader("User-Id") Long userId,
                                                        @RequestParam(defaultValue = "0") @Min(0) int page) {
        return ApiResponse.ok(service.getOrders(userId, page));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDto> getOrder(@RequestHeader("User-Id") Long userId, @PathVariable UUID id) {
        return ApiResponse.ok(service.getOrder(userId, id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderDto> createOrder(@RequestHeader("User-Id") Long userId,
                                             @RequestBody @Valid CreateOrderRequest request) {
        return ApiResponse.ok(service.createOrder(userId, request));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<OrderDto> cancelOrder(@RequestHeader("User-Id") Long userId, @PathVariable UUID id) {
        return ApiResponse.ok(service.cancelOrder(userId, id));
    }

    // ---- admin ----
    @PatchMapping("/{id}/status")
    public ApiResponse<OrderDto> updateStatus(@PathVariable UUID id,
                                              @RequestBody @Valid UpdateOrderStatusRequest request) {
        return ApiResponse.ok(service.updateStatus(id, request.status()));
    }
}