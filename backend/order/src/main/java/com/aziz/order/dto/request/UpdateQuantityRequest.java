package com.aziz.order.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateQuantityRequest(@NotNull @Min(0) @Max(99) Integer quantity) {}