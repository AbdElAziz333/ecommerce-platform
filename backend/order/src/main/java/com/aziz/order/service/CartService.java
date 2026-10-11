package com.aziz.order.service;

import com.aziz.order.client.ProductClient;
import com.aziz.order.dto.request.AddItemRequest;
import com.aziz.order.dto.request.CartOwner;
import com.aziz.order.dto.response.CartDto;
import com.aziz.order.dto.response.CartItemDto;
import com.aziz.order.dto.response.VariantInfo;
import com.aziz.order.model.Cart;
import com.aziz.order.model.CartItem;
import com.aziz.order.repository.CartRepository;
import com.aziz.order.util.exception.BadRequestException;
import com.aziz.order.util.exception.ConflictException;
import com.aziz.order.util.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository repository;
    private final ProductClient productClient;
    private final TransactionTemplate tx;

    // Reads need no transaction: the repository loads items eagerly, and remote calls stay outside any DB transaction.
    public CartDto getCart(CartOwner owner) {
        return find(owner).map(this::toDto).orElse(CartDto.EMPTY);
    }

    public int getItemCount(CartOwner owner) {
        return find(owner).map(Cart::itemCount).orElse(0);
    }

    public CartDto addItem(CartOwner owner, AddItemRequest request) {
        VariantInfo variant = productClient.findVariants(List.of(request.variantId())).stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Product variant not found"));

        if (variant.stockQuantity() < 1) {
            throw new ConflictException("Item is out of stock");
        }

        Cart cart = tx.execute(status -> {
            Cart c = find(owner).orElseGet(() -> repository.save(newCart(owner)));
            c.addItem(request.variantId(), request.quantity());
            return c;
        });

        return toDto(cart);
    }

    public CartDto updateQuantity(CartOwner owner, Long variantId, int quantity) {
        return toDto(tx.execute(status -> {
            Cart c = require(owner);
            c.setQuantity(variantId, quantity);   // 0 removes the item
            return c;
        }));
    }

    public CartDto removeItem(CartOwner owner, Long variantId) {
        return toDto(tx.execute(status -> {
            Cart c = require(owner);
            c.removeItem(variantId);
            return c;
        }));
    }

    public CartDto mergeGuestCart(CartOwner owner) {
        if (!owner.isUser() || owner.guestId() == null) {
            throw new BadRequestException("Both a logged-in user and a guest session are required");
        }

        Cart merged = tx.execute(status -> {
            Optional<Cart> guest = repository.findByGuestId(owner.guestId());
            Optional<Cart> existing = repository.findByUserId(owner.userId());

            if (guest.isEmpty()) {
                return existing.orElse(null);
            }

            if (existing.isEmpty()) {
                guest.get().claimFor(owner.userId());   // the guest cart simply becomes the user's cart
                return guest.get();
            }

            existing.get().mergeFrom(guest.get());
            repository.delete(guest.get());
            return existing.get();
        });

        return merged == null ? CartDto.EMPTY : toDto(merged);
    }

    private Optional<Cart> find(CartOwner o) {
        return o.isUser() ? repository.findByUserId(o.userId()) : repository.findByGuestId(o.guestId());
    }

    private Cart require(CartOwner o) {
        return find(o).orElseThrow(() -> new NotFoundException("Cart not found"));
    }

    private Cart newCart(CartOwner o) {
        return o.isUser() ? Cart.forUser(o.userId()) : Cart.forGuest(o.guestId());
    }

    private CartDto toDto(Cart cart) {
        Map<Long, VariantInfo> variants = lookup(cart.getItems());

        List<CartItemDto> items = cart.getItems().stream()
                .filter(i -> variants.containsKey(i.getVariantId()))   // variant was deleted
                .map(i -> {
                    VariantInfo v = variants.get(i.getVariantId());
                    BigDecimal line = v.price().multiply(BigDecimal.valueOf(i.getQuantity()));
                    return new CartItemDto(v.variantId(), v.productId(), v.productName(), v.slug(), v.sku(),
                            v.options(), i.getQuantity(), v.price(), line, v.stockQuantity() >= i.getQuantity());
                })
                .toList();

        BigDecimal subtotal = items.stream().map(CartItemDto::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartDto(items, items.stream().mapToInt(CartItemDto::quantity).sum(), subtotal);
    }

    private Map<Long, VariantInfo> lookup(List<CartItem> items) {
        if (items.isEmpty()) {
            return Map.of();
        }

        return productClient.findVariants(items.stream().map(CartItem::getVariantId).toList()).stream()
                .collect(Collectors.toMap(VariantInfo::variantId, Function.identity()));
    }
}