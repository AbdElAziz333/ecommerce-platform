package com.aziz.order.client;

import com.aziz.order.dto.response.StockChange;
import com.aziz.order.dto.response.VariantInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product-service", path = "/internal/variants")
public interface ProductClient {
    @GetMapping
    List<VariantInfo> findVariants(@RequestParam("ids") List<Long> ids);

    @PostMapping("/reserve")
    void reserve(@RequestBody List<StockChange> changes);

    @PostMapping("/release")
    void release(@RequestBody List<StockChange> changes);
}