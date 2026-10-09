package com.aziz.gateway.dto.request;

import com.aziz.gateway.util.AddressRules;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAddressRequest(
        @NotBlank @Size(max = 150) String streetLine,
        @Size(max = 50) String label,
        @NotNull @Pattern(regexp = AddressRules.CITY_REGEX, message = AddressRules.CITY_MESSAGE) String city,
        @NotBlank @Size(max = 100) String state,
        @NotBlank @Size(max = 20) String postalCode,
        Boolean defaultShipping
) {
    public CreateAddressRequest {
        if (label == null || label.isBlank()) {
            label = "Home";
        }

        if (defaultShipping == null) {
            defaultShipping = false;
        }
    }
}