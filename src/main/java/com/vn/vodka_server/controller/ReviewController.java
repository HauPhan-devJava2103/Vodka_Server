package com.vn.vodka_server.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.vodka_server.dto.request.CreateReviewRequest;
import com.vn.vodka_server.dto.request.ReplyRequest;
import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.dto.response.PaginationMeta;
import com.vn.vodka_server.dto.response.ReviewResponse;
import com.vn.vodka_server.dto.response.ReviewStatsResponse;
import com.vn.vodka_server.service.ReviewService;
import com.vn.vodka_server.util.PaginationUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // ADMIN: Lấy danh sách đánh giá
    @GetMapping("")
    public ResponseEntity<ApiResponse> getAllReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String rating,
            @RequestParam(required = false) String sort) {

        try {
            Page<ReviewResponse> result = reviewService.getAllReviews(page, pageSize, search, rating, sort);
            PaginationMeta pagination = PaginationUtils.buildPaginationMeta(result, page);

            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Lấy danh sách đánh giá thành công")
                    .data(result.getContent())
                    .pagination(pagination)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }

    }

    // ADMIN: Thống kê đánh giá
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getReviewStats() {
        try {
            ReviewStatsResponse result = reviewService.getReviewStats();
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Thống kê đánh giá thành công")
                    .data(result)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Admin: Phản hồi Review
    @PostMapping("/{id}/reply")
    public ResponseEntity<ApiResponse> replyReview(@PathVariable Long id, @RequestBody ReplyRequest request) {
        try {
            ReviewResponse.ReplyInfo reply = reviewService.replyToReview(id, request);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Đã phản hồi review.")
                    .data(reply)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Admin: Xóa Review + All Replies
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Đã xóa review.")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Admin: Xóa một reply cụ thể
    @DeleteMapping("replies/{id}")
    public ResponseEntity<ApiResponse> deleteReply(@PathVariable Long id) {
        try {
            reviewService.deleteReply(id);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Đã xóa reply.")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Admin: Tạo Review
    @PostMapping("")
    public ResponseEntity<ApiResponse> createReview(@RequestBody CreateReviewRequest request) {
        try {
            ReviewResponse review = reviewService.createReview(request);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Đã tạo review.")
                    .data(review)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
