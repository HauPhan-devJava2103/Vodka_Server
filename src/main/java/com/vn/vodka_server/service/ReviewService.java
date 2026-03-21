package com.vn.vodka_server.service;

import java.util.List;

import com.vn.vodka_server.dto.request.CreateReplyRequest;
import com.vn.vodka_server.dto.response.ReviewReplyResponse;

public interface ReviewService {

    // Tạo phản hồi cho một review
    ReviewReplyResponse replyToReview(Long reviewId, String email, CreateReplyRequest request);

    // Lấy danh sách phản hồi của một review
    List<ReviewReplyResponse> getReplies(Long reviewId);
}
