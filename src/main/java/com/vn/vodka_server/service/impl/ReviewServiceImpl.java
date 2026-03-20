package com.vn.vodka_server.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vn.vodka_server.dto.request.CreateReplyRequest;
import com.vn.vodka_server.dto.response.ReviewReplyResponse;
import com.vn.vodka_server.exception.ResourceNotFoundException;
import com.vn.vodka_server.model.Review;
import com.vn.vodka_server.model.ReviewReply;
import com.vn.vodka_server.model.User;
import com.vn.vodka_server.repository.ReviewReplyRepository;
import com.vn.vodka_server.repository.ReviewRepository;
import com.vn.vodka_server.repository.UserRepository;
import com.vn.vodka_server.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewReplyRepository reviewReplyRepository;
    private final UserRepository userRepository;

    // Tạo phản hồi cho một review
    @Override
    public ReviewReplyResponse replyToReview(Long reviewId, String email, CreateReplyRequest request) {

        // Tìm review cha, báo lỗi nếu không tồn tại
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy đánh giá với id = " + reviewId));

        // Tìm user đang đăng nhập
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy tài khoản với email: " + email));

        // Tạo và lưu phản hồi mới
        ReviewReply reply = ReviewReply.builder()
                .content(request.getContent())
                .review(review)
                .user(user)
                .build();

        ReviewReply saved = reviewReplyRepository.save(reply);

        return mapToReplyResponse(saved);
    }

    // Lấy danh sách phản hồi của một review
    @Override
    public List<ReviewReplyResponse> getReplies(Long reviewId) {

        // Kiểm tra review có tồn tại không
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException(
                    "Không tìm thấy đánh giá với id = " + reviewId);
        }

        // Lấy danh sách replies
        return reviewReplyRepository
                .findByReviewIdOrderByCreatedAtAsc(reviewId)
                .stream()
                .map(this::mapToReplyResponse)
                .toList();
    }

    // HELPER: Map ReviewReply entity sang DTO
    private ReviewReplyResponse mapToReplyResponse(ReviewReply reply) {
        return ReviewReplyResponse.builder()
                .id(String.valueOf(reply.getId()))
                .userId(String.valueOf(reply.getUser().getId()))
                .userName(reply.getUser().getFullName())
                .avatarUrl(reply.getUser().getAvatarUrl())
                .content(reply.getContent())
                .createdAt(reply.getCreatedAt() != null
                        ? reply.getCreatedAt().toInstant().toString()
                        : null)
                .build();
    }
}
