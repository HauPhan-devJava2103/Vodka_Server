package com.vn.vodka_server.service.impl;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vn.vodka_server.dto.request.ChangePasswordRequest;
import com.vn.vodka_server.dto.request.UpdateHistoryRequest;
import com.vn.vodka_server.dto.request.UpdateProfileRequest;
import com.vn.vodka_server.dto.response.FeaturedMovieResponse;
import com.vn.vodka_server.dto.response.GenreResponse;
import com.vn.vodka_server.dto.response.ReviewResponse;
import com.vn.vodka_server.dto.response.TagResponse;
import com.vn.vodka_server.dto.response.UserInfo;
import com.vn.vodka_server.dto.response.WatchHistoryResponse;
import com.vn.vodka_server.model.Favorite;
import com.vn.vodka_server.model.Genre;
import com.vn.vodka_server.model.Movie;
import com.vn.vodka_server.model.Review;
import com.vn.vodka_server.model.Tag;
import com.vn.vodka_server.model.User;
import com.vn.vodka_server.model.WatchHistory;
import com.vn.vodka_server.repository.FavoriteRepository;
import com.vn.vodka_server.repository.MovieRepository;
import com.vn.vodka_server.repository.ReviewRepository;
import com.vn.vodka_server.repository.UserRepository;
import com.vn.vodka_server.repository.WatchHistoryRepository;
import com.vn.vodka_server.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FavoriteRepository favoriteRepository;
    private final WatchHistoryRepository watchHistoryRepository;
    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;

    @Override
    public UserInfo updateProfile(String email, UpdateProfileRequest request) {
        User user = findUserByEmail(email);

        // Cập nhật các field
        user.setFullName(request.getDisplayName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        userRepository.save(user);

        return UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .dateOfBirth(user.getDateOfBirth())
                .avatarUrl(user.getAvatarUrl())
                .provider(user.getProvider() != null ? user.getProvider().name() : "LOCAL")
                .build();
    }

    @Override
    public UserInfo getProfile(String email) {
        User user = findUserByEmail(email);

        return UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .provider(user.getProvider() != null ? user.getProvider().name() : "LOCAL")
                .role(user.getRole())
                .build();
    }

    @Override
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không được trùng với mật khẩu mới");
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu mới
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new RuntimeException("Mật khẩu mới và xác nhận mật khẩu mới không khớp");
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public Page<FeaturedMovieResponse> getFavorites(String email, int page, int pageSize) {
        validatePagination(page, pageSize);
        User user = findUserByEmail(email);

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());

        Page<Favorite> favoritePage = favoriteRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        return favoritePage.map(fav -> mapToFeaturedMovieResponse(fav.getMovie()));

    }

    @Override
    public Page<WatchHistoryResponse> getHistory(String email, int page, int pageSize) {
        validatePagination(page, pageSize);
        User user = findUserByEmail(email);

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("watchedAt").descending());

        Page<WatchHistory> watchHistoryPage = watchHistoryRepository.findByUserOrderByWatchedAtDesc(user, pageable);

        return watchHistoryPage.map(this::mapToWatchHistoryResponse);
    }

    @Override
    public Page<ReviewResponse> getReviews(String email, int page, int pageSize) {
        validatePagination(page, pageSize);
        User user = findUserByEmail(email);

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());

        Page<Review> reviewPage = reviewRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        return reviewPage.map(this::mapToReviewResponse);
    }

    @Override
    public void updateHistory(String email, UpdateHistoryRequest request) {
        User user = findUserByEmail(email);
        Movie movie = findMovieById(request.getMovieId());

        Optional<WatchHistory> watchHistory = watchHistoryRepository.findByUserAndMovie(user, movie);

        if (watchHistory.isPresent()) {
            watchHistory.get().setWatchedAt(new Date());
            watchHistoryRepository.save(watchHistory.get());
        } else {
            WatchHistory newWatchHistory = WatchHistory.builder()
                    .user(user)
                    .movie(movie)
                    .watchedAt(new Date())
                    .build();
            watchHistoryRepository.save(newWatchHistory);
        }
    }

    // HELPER METHOD

    private void validatePagination(int page, int pageSize) {
        if (page < 1)
            throw new RuntimeException("Số trang phải bắt đầu từ 1");
        if (pageSize <= 0 || pageSize > 50)
            throw new RuntimeException("Số lượng phim mỗi trang phải từ 1 đến 50");
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
    }

    private Movie findMovieById(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Phim không tồn tại"));
    }

    private FeaturedMovieResponse mapToFeaturedMovieResponse(Movie movie) {
        return FeaturedMovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .posterUrl(movie.getPostUrl())
                .bannerUrl(movie.getBannerUrl())
                .releaseYear(movie.getReleaseYear())
                .genre(movie.getGenres().stream()
                        .map(this::mapToGenreResponse)
                        .collect(Collectors.toList()))
                .rating(movie.getRating())
                .tags(movie.getTags().stream()
                        .map(this::mapToTagResponse)
                        .collect(Collectors.toList()))
                .description(movie.getDescription())
                .build();
    }

    private WatchHistoryResponse mapToWatchHistoryResponse(WatchHistory watchHistory) {
        Movie movie = watchHistory.getMovie();
        return WatchHistoryResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .posterUrl(movie.getPostUrl())
                .bannerUrl(movie.getBannerUrl())
                .releaseYear(movie.getReleaseYear())
                .genre(movie.getGenres().stream()
                        .map(this::mapToGenreResponse)
                        .collect(Collectors.toList()))
                .rating(movie.getRating())
                .tags(movie.getTags().stream()
                        .map(this::mapToTagResponse)
                        .collect(Collectors.toList()))
                .description(movie.getDescription())
                .watchedAt(watchHistory.getWatchedAt() != null
                        ? watchHistory.getWatchedAt().toInstant().toString()
                        : null)
                .build();
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt() != null
                        ? review.getCreatedAt().toInstant().toString()
                        : null)
                .movie(mapToFeaturedMovieResponse(review.getMovie()))
                .build();
    }

    private GenreResponse mapToGenreResponse(Genre genre) {
        return GenreResponse.builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();
    }

    private TagResponse mapToTagResponse(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }

}
