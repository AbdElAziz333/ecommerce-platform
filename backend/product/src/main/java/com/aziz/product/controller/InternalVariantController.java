package com.aziz.product.controller;

import com.aziz.product.dto.request.StockChange;
import com.aziz.product.dto.response.VariantInfo;
import com.aziz.product.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/variants")
@RequiredArgsConstructor
public class InternalVariantController {
    private final InventoryService service;

    @GetMapping
    public List<VariantInfo> find(@RequestParam List<Long> ids) {
        return service.find(ids);
    }

    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reserve(@RequestBody List<StockChange> changes) {
        service.reserve(changes);
    }

    @PostMapping("/release")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void release(@RequestBody List<StockChange> changes) {
        service.release(changes);
    }
}