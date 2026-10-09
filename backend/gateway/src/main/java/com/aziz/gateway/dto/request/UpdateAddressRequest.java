package com.aziz.gateway.dto.request;

import com.aziz.gateway.util.AddressRules;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// null = "not provided"; if provided, it must not be blank
public record UpdateAddressRequest(
        @Pattern(regexp = AddressRules.NOT_BLANK_REGEX) @Size(max = 150) String streetLine,
        @Pattern(regexp = AddressRules.NOT_BLANK_REGEX) @Size(max = 50) String label,
        @Pattern(regexp = AddressRules.CITY_REGEX, message = AddressRules.CITY_MESSAGE) String city,
        @Pattern(regexp = AddressRules.NOT_BLANK_REGEX) @Size(max = 100) String state,
        @Pattern(regexp = AddressRules.NOT_BLANK_REGEX) @Size(max = 20) String postalCode,
        Boolean defaultShipping
) {}