package com.aziz.gateway.dto.request;

import com.aziz.gateway.util.ValidationRules;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Pattern(regexp = ValidationRules.NOT_BLANK_REGEX) @Size(max = 30) String firstName,
        @Pattern(regexp = ValidationRules.NOT_BLANK_REGEX) @Size(max = 30) String lastName,
        @Pattern(regexp = ValidationRules.PHONE_REGEX, message = "must be 11-13 digits, optional leading +") String phoneNumber
) {}