package com.vn.vodka_server.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private UserInfo userInfo;
}
