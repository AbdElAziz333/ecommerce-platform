package com.aziz.product.service;

import com.aziz.product.dto.request.StockChange;
import com.aziz.product.dto.response.VariantInfo;
import com.aziz.product.repository.ProductVariantRepository;
import com.aziz.product.util.exceptions.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final ProductVariantRepository repository;

    @Transactional(readOnly = true)
    public List<VariantInfo> find(Collection<Long> ids) {
        return repository.findAllWithProductByIdIn(ids).stream()
                .map(v -> new VariantInfo(v.getId(), v.getProduct().getId(), v.getProduct().getName(),
                        v.getProduct().getSlug(), v.getSku(), v.getPrice(), v.getStockQuantity(),
                        Map.copyOf(v.getOptionValues())))
                .toList();
    }

    /** All-or-nothing: the exception rolls back every decrement made so far. */
    @Transactional
    public void reserve(List<StockChange> changes) {
        for (StockChange c : sorted(changes)) {
            if (repository.decrementStock(c.variantId(), c.quantity()) == 0) {
                throw new ConflictException("Insufficient stock for variant " + c.variantId());
            }
        }
    }

    @Transactional
    public void release(List<StockChange> changes) {
        sorted(changes).forEach(c -> repository.incrementStock(c.variantId(), c.quantity()));
    }

    // consistent lock order avoids deadlocks between concurrent reservations
    private static List<StockChange> sorted(List<StockChange> changes) {
        return changes.stream().sorted(Comparator.comparing(StockChange::variantId)).toList();
    }
}