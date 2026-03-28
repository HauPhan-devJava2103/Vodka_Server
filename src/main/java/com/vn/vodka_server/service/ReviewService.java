package com.vn.vodka_server.service;

import org.springframework.data.domain.Page;

import com.vn.vodka_server.dto.request.CreateReviewRequest;
import com.vn.vodka_server.dto.request.ReplyRequest;
import com.vn.vodka_server.dto.response.ReviewResponse;
import com.vn.vodka_server.dto.response.ReviewStatsResponse;

public interface ReviewService {

    // Admin lấy danh sách review
    Page<ReviewResponse> getAllReviews(int page, int pageSize, String search, String rating, String sort);

    // Admin thống kê review
    ReviewStatsResponse getReviewStats();

    // Admin xóa review
    void deleteReview(Long id);

    // Admin xóa reply
    void deleteReply(Long id);

    // Admin phản hồi review
    ReviewResponse.ReplyInfo replyToReview(Long reviewId, ReplyRequest content);

    ReviewResponse createReview(CreateReviewRequest request);

}
