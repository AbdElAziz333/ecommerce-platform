package com.aziz.gateway.dto.response;

import java.util.List;

public record CurrentUserDto(
        Long userId,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String role,
        String preferredLanguage,
        List<AddressDto> addresses
) {}