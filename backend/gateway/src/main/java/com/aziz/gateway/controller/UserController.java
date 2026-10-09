package com.aziz.gateway.controller;

import com.aziz.gateway.dto.request.ChangePasswordRequest;
import com.aziz.gateway.dto.response.CurrentUserDto;
import com.aziz.gateway.dto.response.UserDto;
import com.aziz.gateway.dto.request.UpdateUserRequest;
import com.aziz.gateway.service.AuthCookieService;
import com.aziz.gateway.service.UserService;
import com.aziz.gateway.util.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    private final AuthCookieService cookieService;

    // ---- current user ----

    @GetMapping("/current")
    public ApiResponse<CurrentUserDto> getCurrentUser(@AuthenticationPrincipal Long userId) {
        return ApiResponse.ok(service.getCurrentUser(userId));
    }

    @PatchMapping("/current")
    public ApiResponse<UserDto> updateCurrentUser(@AuthenticationPrincipal Long userId, @RequestBody @Valid UpdateUserRequest request) {
        return ApiResponse.ok(service.updateUser(userId, request));
    }

    @PutMapping("/current/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal Long userId, @RequestBody @Valid ChangePasswordRequest request) {
        service.changePassword(userId, request);
    }

    @DeleteMapping("/current")
    public ResponseEntity<Void> deleteCurrentUser(@AuthenticationPrincipal Long userId) {
        service.deleteUser(userId);
        return ResponseEntity.noContent().headers(cookieService.clearHeaders()).build();
    }

    // ---- admin only ----

    @GetMapping
    public ApiResponse<Page<UserDto>> getUsers(@RequestParam(defaultValue = "0") @Min(0) int page) {
        return ApiResponse.ok(service.getUsers(page));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserDto> getUserById(@PathVariable Long id) {
        return ApiResponse.ok(service.getUserById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
    }
}