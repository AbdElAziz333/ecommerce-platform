package com.aziz.gateway.controller;

import com.aziz.gateway.dto.request.CreateAddressRequest;
import com.aziz.gateway.dto.request.UpdateAddressRequest;
import com.aziz.gateway.dto.response.AddressDto;
import com.aziz.gateway.service.AddressService;
import com.aziz.gateway.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService service;

    @GetMapping
    public ApiResponse<List<AddressDto>> getAddresses(@AuthenticationPrincipal Long userId) {
        return ApiResponse.ok(service.getAddresses(userId));
    }

    @GetMapping("/{addressId}")
    public ApiResponse<AddressDto> getAddressById(@AuthenticationPrincipal Long userId, @PathVariable Long addressId) {
        return ApiResponse.ok(service.getAddressById(userId, addressId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AddressDto> createAddress(@AuthenticationPrincipal Long userId, @RequestBody @Valid CreateAddressRequest request) {
        return ApiResponse.ok(service.createAddress(userId, request));
    }

    @PatchMapping("/{addressId}")
    public ApiResponse<AddressDto> updateAddress(@AuthenticationPrincipal Long userId, @PathVariable Long addressId, @RequestBody @Valid UpdateAddressRequest request) {
        return ApiResponse.ok(service.updateAddress(userId, addressId, request));
    }

    @DeleteMapping("/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@AuthenticationPrincipal Long userId, @PathVariable Long addressId) {
        service.deleteAddress(userId, addressId);
    }
}