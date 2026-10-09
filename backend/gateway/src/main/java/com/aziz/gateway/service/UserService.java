package com.aziz.gateway.service;

import com.aziz.gateway.dto.request.ChangePasswordRequest;
import com.aziz.gateway.dto.response.CurrentUserDto;
import com.aziz.gateway.dto.response.UserDto;
import com.aziz.gateway.mapper.UserMapper;
import com.aziz.gateway.model.User;
import com.aziz.gateway.repository.RefreshTokenRepository;
import com.aziz.gateway.repository.UserRepository;
import com.aziz.gateway.dto.request.UpdateUserRequest;
import com.aziz.gateway.util.exceptions.BadRequestException;
import com.aziz.gateway.util.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private static final int PAGE_SIZE = 100;

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;
    private final AddressService addressService;
    private final RefreshTokenRepository refreshTokens;

    @Transactional(readOnly = true)
    public Page<UserDto> getUsers(int page) {
        return repository.findAll(PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt")))
                .map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return mapper.toDto(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public CurrentUserDto getCurrentUser(Long id) {
        return mapper.toCurrentUserDto(findOrThrow(id), addressService.getAddresses(id));
    }

    @Transactional
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        User user = findOrThrow(id);

        // PATCH: only apply fields that were sent
        if (request.firstName() != null)   user.setFirstName(request.firstName());
        if (request.lastName() != null)    user.setLastName(request.lastName());
        if (request.phoneNumber() != null) user.setPhoneNumber(request.phoneNumber());

        return mapper.toDto(user);
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = findOrThrow(id);
        if (!encoder.matches(request.currentPassword(), user.getPassword())) {
            // 400 on purpose: a 401 would make the frontend think the session expired
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPassword(encoder.encode(request.newPassword()));
        refreshTokens.deleteAllForUser(id);   // sign out everywhere
        log.info("Password changed for user {}", id);
    }

    @Transactional
    public void deleteUser(Long id) {
        repository.delete(findOrThrow(id));   // addresses are removed by ON DELETE CASCADE
        refreshTokens.deleteAllForUser(id);
        log.info("User {} deleted", id);
    }

    private User findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }
}