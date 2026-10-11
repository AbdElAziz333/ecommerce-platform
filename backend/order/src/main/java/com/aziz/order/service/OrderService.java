package com.aziz.order.service;

import com.aziz.order.client.ProductClient;
import com.aziz.order.dto.response.*;
import com.aziz.order.model.Cart;
import com.aziz.order.model.CartItem;
import com.aziz.order.model.OrderItem;
import com.aziz.order.dto.request.CreateOrderRequest;
import com.aziz.order.mapper.OrderMapper;
import com.aziz.order.model.Order;
import com.aziz.order.repository.CartRepository;
import com.aziz.order.repository.OrderRepository;
import com.aziz.order.util.enums.OrderStatus;
import com.aziz.order.util.exception.*;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private static final int PAGE_SIZE = 20;

    private final OrderRepository repository;
    private final CartRepository cartRepository;
    private final ProductClient productClient;
    private final OrderMapper mapper;
    private final OrderProperties properties;
    private final TransactionTemplate tx;

    public Page<OrderSummaryDto> getOrders(Long userId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        return repository.findByUserId(userId, pageable).map(mapper::toSummary);
    }

    public OrderDto getOrder(Long userId, UUID id) {
        return mapper.toDto(findOwned(userId, id));
    }

    public OrderDto createOrder(Long userId, CreateOrderRequest request) {
        List<StockChange> reserved = new ArrayList<>();

        try {
            Order order = tx.execute(status -> place(userId, request, reserved));
            log.info("Order {} created for user {}", order.getOrderNumber(), userId);
            return mapper.toDto(order);
        } catch (RuntimeException e) {
            if (!reserved.isEmpty()) {
                release(reserved);
            }

            throw e;
        }
    }

    public OrderDto cancelOrder(Long userId, UUID id) {
        return changeStatus(() -> findOwned(userId, id), OrderStatus.CANCELLED);
    }

    public OrderDto updateStatus(UUID id, OrderStatus next) {
        return changeStatus(() -> repository.findWithItemsById(id).orElseThrow(() -> new NotFoundException("Order not found: " + id)), next);
    }

    private Order place(Long userId, CreateOrderRequest request, List<StockChange> reserved) {
        Cart cart = cartRepository.findByUserIdForUpdate(userId)
                .filter(c -> !c.getItems().isEmpty())
                .orElseThrow(() -> new BadRequestException("Your cart is empty"));

        Map<Long, VariantInfo> variants = productClient
                .findVariants(cart.getItems().stream().map(CartItem::getVariantId).toList()).stream()
                .collect(Collectors.toMap(VariantInfo::variantId, Function.identity()));

        List<OrderItem> items = cart.getItems().stream().map(ci -> {
            VariantInfo v = variants.get(ci.getVariantId());

            if (v == null) {
                throw new BadRequestException("Some items in your cart are no longer available");
            }

            return OrderItem.snapshot(v, ci.getQuantity());
        }).toList();

        List<StockChange> changes = items.stream()
                .map(i -> new StockChange(i.getVariantId(), i.getQuantity()))
                .toList();
        reserve(changes);
        reserved.addAll(changes);

        Order order = repository.save(Order.place(userId, request.shippingAddress(), request.paymentMethod(),
                properties.shippingFee(), request.notes(), items));

        cart.clear();
        return order;
    }

    private OrderDto changeStatus(Supplier<Order> lookup, OrderStatus next) {
        Order order = tx.execute(status -> {
            Order o = lookup.get();
            // validates the transition; @Version makes concurrent changes fail
            o.changeStatus(next);
            return o;
        });

        // reached exactly once per order, thanks to the transition rules
        if (next == OrderStatus.CANCELLED) {
            release(order.getItems().stream().map(i -> new StockChange(i.getVariantId(), i.getQuantity())).toList());
        }

        log.info("Order {} is now {}", order.getOrderNumber(), order.getStatus());
        return mapper.toDto(order);
    }

    private void reserve(List<StockChange> changes) {
        try {
            productClient.reserve(changes);
        } catch (FeignException.Conflict e) {
            throw new ConflictException("Some items are out of stock");
        }
    }

    private void release(List<StockChange> changes) {
        try {
            productClient.release(changes);
        } catch (RuntimeException e) {
            log.error("Failed to release stock {}. Manual reconciliation needed", changes, e);
        }
    }

    private Order findOwned(Long userId, UUID id) {
        return repository.findWithItemsByIdAndUserId(id, userId).orElseThrow(() -> new NotFoundException("Order not found: " + id));
    }
}