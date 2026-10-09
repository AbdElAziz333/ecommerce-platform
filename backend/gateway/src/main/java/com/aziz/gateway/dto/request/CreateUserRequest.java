package com.aziz.gateway.dto.request;

import com.aziz.gateway.util.ValidationRules;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Size(max = 30) String firstName,
        @NotBlank @Size(max = 30) String lastName,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank @Pattern(regexp = ValidationRules.PHONE_REGEX, message = "must be 11-13 digits, optional leading +") String phoneNumber
) {}