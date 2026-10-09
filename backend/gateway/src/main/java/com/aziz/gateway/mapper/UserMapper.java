package com.aziz.gateway.mapper;

import com.aziz.gateway.dto.response.AddressDto;
import com.aziz.gateway.dto.response.CurrentUserDto;
import com.aziz.gateway.dto.response.UserDto;
import com.aziz.gateway.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    public UserDto toDto(User u) {
        return new UserDto(u.getId(), u.getFirstName(), u.getLastName());
    }

    public CurrentUserDto toCurrentUserDto(User u, List<AddressDto> addresses) {
        return new CurrentUserDto(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(),
                u.getPhoneNumber(), u.getRole(), u.getPreferredLanguage(), addresses);
    }
}