package com.vn.vodka_server.service;

import com.vn.vodka_server.dto.request.UpdateProfileRequest;
import com.vn.vodka_server.dto.response.UserInfo;

public interface UserService {
    UserInfo updateProfile(String email, UpdateProfileRequest request);
}
