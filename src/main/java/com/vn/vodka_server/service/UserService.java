package com.vn.vodka_server.service;

import org.springframework.data.domain.Page;

import com.vn.vodka_server.dto.request.ChangePasswordRequest;
import com.vn.vodka_server.dto.request.UpdateHistoryRequest;
import com.vn.vodka_server.dto.request.UpdateProfileRequest;
import com.vn.vodka_server.dto.response.FeaturedMovieResponse;
import com.vn.vodka_server.dto.response.ReviewResponse;
import com.vn.vodka_server.dto.response.UserInfo;
import com.vn.vodka_server.dto.response.WatchHistoryResponse;

public interface UserService {
    UserInfo updateProfile(String email, UpdateProfileRequest request);

    UserInfo getProfile(String email);

    void changePassword(String email, ChangePasswordRequest request);

    Page<FeaturedMovieResponse> getFavorites(String email, int page, int pageSize);

    Page<WatchHistoryResponse> getHistory(String email, int page, int pageSize);

    Page<ReviewResponse> getReviews(String email, int page, int pageSize);

    void updateHistory(String email, UpdateHistoryRequest request);

}
