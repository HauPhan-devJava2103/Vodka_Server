package com.vn.vodka_server.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.vodka_server.dto.request.UpdateProfileRequest;
import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.dto.response.UpdateProfileResponse;
import com.vn.vodka_server.dto.response.UserInfo;
import com.vn.vodka_server.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/me/profile")
    public ResponseEntity<ApiResponse> updateProfile(
            Principal principal,
            @RequestBody UpdateProfileRequest request) {
        try {
            UserInfo updatedUser = userService.updateProfile(principal.getName(), request);
            return ResponseEntity
                    .ok(ApiResponse.success("Cập nhật thành công", new UpdateProfileResponse(updatedUser)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
