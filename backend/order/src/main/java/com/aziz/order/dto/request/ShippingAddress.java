package com.aziz.order.dto.request;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Embeddable
public record ShippingAddress(
        @NotBlank @Size(max = 150) String streetLine,
        @NotNull @Pattern(
                regexp = "ALEXANDRIA|EL_BEHEIRA|CAIRO|TANTA",
                message = "must be one of ALEXANDRIA, EL_BEHEIRA, CAIRO, TANTA"
        )
        String city,
        @NotBlank @Size(max = 100) String state,
        @NotBlank @Size(max = 20) String postalCode
) {}