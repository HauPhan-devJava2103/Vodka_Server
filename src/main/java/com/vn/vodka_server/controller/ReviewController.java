package com.vn.vodka_server.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.vodka_server.dto.request.CreateReplyRequest;
import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.dto.response.ReviewReplyResponse;
import com.vn.vodka_server.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // API: Tạo phản hồi cho một review (yêu cầu đăng nhập)
    @PostMapping("/{reviewId}/replies")
    public ResponseEntity<ApiResponse> replyToReview(
            @PathVariable Long reviewId,
            Principal principal,
            @Valid @RequestBody CreateReplyRequest request) {
        try {
            ReviewReplyResponse reply = reviewService.replyToReview(reviewId, principal.getName(), request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Phản hồi đã được tạo thành công", reply));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    // API: Lấy danh sách phản hồi của một review (public)
    @GetMapping("/{reviewId}/replies")
    public ResponseEntity<ApiResponse> getReplies(@PathVariable Long reviewId) {
        try {
            List<ReviewReplyResponse> replies = reviewService.getReplies(reviewId);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách phản hồi thành công", replies));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}
