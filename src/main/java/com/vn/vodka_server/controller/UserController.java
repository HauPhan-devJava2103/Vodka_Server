package com.vn.vodka_server.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.vodka_server.dto.request.ChangePasswordRequest;
import com.vn.vodka_server.dto.request.UpdateProfileRequest;
import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.dto.response.FeaturedMovieResponse;
import com.vn.vodka_server.dto.response.PaginationMeta;
import com.vn.vodka_server.dto.response.ReviewResponse;
import com.vn.vodka_server.dto.response.UpdateProfileResponse;
import com.vn.vodka_server.dto.response.UserInfo;
import com.vn.vodka_server.dto.response.WatchHistoryResponse;
import com.vn.vodka_server.service.UserService;
import com.vn.vodka_server.util.PaginationUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Cập nhật thông tin cá nhân
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

    // Lấy thông tin User
    @GetMapping("/me/profile")
    public ResponseEntity<ApiResponse> getProfile(Principal principal) {
        try {
            UserInfo userInfo = userService.getProfile(principal.getName());
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin thành công",
                    userInfo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Đổi mật khẩu
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            Principal principal,
            @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(principal.getName(), request);
            return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Phim yêu thích
    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse> getFavorites(Principal principal, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Page<FeaturedMovieResponse> resultPage = userService.getFavorites(principal.getName(), page, pageSize);
            PaginationMeta meta = PaginationUtils.buildPaginationMeta(resultPage, page);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Lấy danh sách phim yêu thích thành công")
                    .data(resultPage.getContent())
                    .pagination(meta)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Lịch sử xem phim của tôi
    @GetMapping("/history")
    public ResponseEntity<ApiResponse> getHistory(Principal principal, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Page<WatchHistoryResponse> resultPage = userService.getHistory(principal.getName(), page, pageSize);
            PaginationMeta meta = PaginationUtils.buildPaginationMeta(resultPage, page);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Lấy lịch sử đã xem thành công")
                    .data(resultPage.getContent())
                    .pagination(meta)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Lấy danh sách đánh giá của tôi
    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse> getReviews(Principal principal, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Page<ReviewResponse> resultPage = userService.getReviews(principal.getName(), page, pageSize);
            PaginationMeta meta = PaginationUtils.buildPaginationMeta(resultPage, page);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Lấy danh sách đánh giá thành công")
                    .data(resultPage.getContent())
                    .pagination(meta)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

}
