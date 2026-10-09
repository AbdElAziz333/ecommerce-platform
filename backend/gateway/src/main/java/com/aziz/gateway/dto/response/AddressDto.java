package com.aziz.gateway.dto.response;

public record AddressDto(
        Long id,
        String label,
        String streetLine,
        String city,
        String state,
        String postalCode,
        Boolean defaultShipping
) {}