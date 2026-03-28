package com.vn.vodka_server.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.vodka_server.dto.request.CreateReviewRequest;
import com.vn.vodka_server.dto.request.ReplyRequest;
import com.vn.vodka_server.dto.response.ReviewResponse;
import com.vn.vodka_server.dto.response.ReviewStatsResponse;
import com.vn.vodka_server.model.Movie;
import com.vn.vodka_server.model.Review;
import com.vn.vodka_server.model.ReviewReply;
import com.vn.vodka_server.model.User;
import com.vn.vodka_server.repository.MovieRepository;
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
    private final MovieRepository movieRepository;

    @Override
    public Page<ReviewResponse> getAllReviews(int page, int pageSize, String search, String rating, String sort) {

        Sort sortObject = parseSort(sort);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sortObject);

        // empty string → null, "all" → null
        Double ratingValue = null;
        if (rating != null && !rating.isBlank() && !"all".equalsIgnoreCase(rating)) {
            ratingValue = Double.parseDouble(rating);
        }

        Page<Review> reviewPage = reviewRepository.findAdminReviews(search, ratingValue, pageable);
        return reviewPage.map(this::mapToReviewResponse);
    }

    @Override
    public ReviewStatsResponse getReviewStats() {

        long totalReviews = reviewRepository.count();
        double averageRating = reviewRepository.averageRating();
        long moviesWithReviews = reviewRepository.countDistinctMovie();
        long totalReplies = reviewReplyRepository.count();

        // Thời điểm hiện tại
        LocalDateTime endOfThisMonth = LocalDateTime.now();

        // Đầu tháng này (Lấy ngày hiện tại, chuyển về mùng 1, thời gian 00:00:00)
        LocalDateTime startOfThisMonth = endOfThisMonth.toLocalDate().atStartOfDay().withDayOfMonth(1);

        // Đầu tháng trước (Lùi lại đúng 1 tháng từ mốc đầu tháng này)
        LocalDateTime startOfLastMonth = startOfThisMonth.minusMonths(1);

        // Cuối tháng trước (Dùng chính mốc đầu tháng này làm điểm dừng chặn trên)
        LocalDateTime endOfLastMonth = startOfThisMonth;

        // Đếm review trong tháng này vs tháng trước
        long reviewsThisMonth = reviewRepository.countByCreatedAtBetween(startOfThisMonth, endOfThisMonth);
        long reviewsLastMonth = reviewRepository.countByCreatedAtBetween(startOfLastMonth, endOfLastMonth);

        // Đếm replies trong tháng này vs tháng trước
        long repliesThisMonth = reviewReplyRepository.countByCreatedAtBetween(startOfThisMonth, endOfThisMonth);
        long repliesLastMonth = reviewReplyRepository.countByCreatedAtBetween(startOfLastMonth, endOfLastMonth);

        // Tính phần trăm tăng/giảm
        double reviewsTrendPercent = calculateTrendPercent(reviewsThisMonth, reviewsLastMonth);
        double repliesTrendPercent = calculateTrendPercent(repliesThisMonth, repliesLastMonth);

        return ReviewStatsResponse.builder()
                .totalReviews(totalReviews)
                .averageRating(Math.round(averageRating * 10.0) / 10.0)
                .moviesWithReviews(moviesWithReviews)
                .totalReplies(totalReplies)
                .trends(ReviewStatsResponse.TrendInfo.builder()
                        .reviewsTrendPercent(reviewsTrendPercent)
                        .repliesTrendPercent(repliesTrendPercent)
                        .build())
                .build();

    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy review"));
        // Xoá review
        reviewRepository.delete(review);
    }

    @Override
    public void deleteReply(Long id) {
        ReviewReply reply = reviewReplyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy reply"));
        // Xoá reply
        reviewReplyRepository.delete(reply);
    }

    @Override
    public ReviewResponse.ReplyInfo replyToReview(Long reviewId, ReplyRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy admin"));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy review"));

        // Lưu Reply vào DB
        ReviewReply reply = ReviewReply.builder()
                .review(review)
                .user(admin)
                .content(request.getContent())
                .build();
        reviewReplyRepository.save(reply);

        return ReviewResponse.ReplyInfo.builder()
                .id(reply.getId())
                .userName(admin.getFullName())
                .avatarUrl(admin.getAvatarUrl())
                .content(reply.getContent())
                .createdAt(formatDate(reply.getCreatedAt()))
                .build();

    }

    @Override
    public ReviewResponse createReview(CreateReviewRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy admin"));
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim"));

        Integer ratingStar = request.getRating();
        Double ratingDouble = 0.0;
        if (ratingStar != null) {
            ratingDouble = ratingStar.doubleValue();
        }

        Review review = Review.builder()
                .movie(movie)
                .user(admin)
                .rating(ratingDouble)
                .content(request.getContent())
                .build();

        reviewRepository.save(review);

        return mapToReviewResponse(review);
    }

    // HELPER METHOD

    private Sort parseSort(String sort) {
        if (sort == null)
            return Sort.by("createdAt").descending();

        return switch (sort) {
            case "createdAt_desc" -> Sort.by("createdAt").descending();
            case "createdAt_asc" -> Sort.by("createdAt").ascending();
            case "rating_desc" -> Sort.by("rating").descending();
            case "rating_asc" -> Sort.by("rating").ascending();
            default -> Sort.by("createdAt").descending();
        };
    }

    private ReviewResponse mapToReviewResponse(Review review) {

        // Map replies
        List<ReviewResponse.ReplyInfo> replies = review.getReplies().stream()
                .map(reply -> ReviewResponse.ReplyInfo.builder()
                        .id(reply.getId())
                        .userName(reply.getUser().getFullName())
                        .avatarUrl(reply.getUser().getAvatarUrl())
                        .content(reply.getContent())
                        .createdAt(formatDate(reply.getCreatedAt()))
                        .build())
                .toList();

        return ReviewResponse.builder()
                .id(review.getId())
                .userName(review.getUser().getFullName())
                .avatarUrl(review.getUser().getAvatarUrl())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(formatDate(review.getCreatedAt()))
                .movieId(review.getMovie().getId())
                .movieTitle(review.getMovie().getTitle())
                .replied(replies)
                .build();
    }

    private String formatDate(Date date) {
        if (date == null)
            return "N/A";
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    private double calculateTrendPercent(long current, long previous) {
        if (previous == 0)
            return current > 0 ? 100 : 0.0;
        return Math.round((current - previous) * 1000.0 / previous) / 10.0;
    }

}
