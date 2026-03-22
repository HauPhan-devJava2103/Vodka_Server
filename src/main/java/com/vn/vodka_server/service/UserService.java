package com.vn.vodka_server.service;

import com.vn.vodka_server.dto.request.ChangePasswordRequest;
import com.vn.vodka_server.dto.request.UpdateProfileRequest;
import com.vn.vodka_server.dto.response.UserInfo;

public interface UserService {
    UserInfo updateProfile(String email, UpdateProfileRequest request);

    UserInfo getProfile(String email);

    void changePassword(String email, ChangePasswordRequest request);
}
