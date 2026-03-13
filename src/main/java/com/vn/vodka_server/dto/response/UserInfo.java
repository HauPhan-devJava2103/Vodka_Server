package com.vn.vodka_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfo {
    private Long id;
    private String email;
    private String fullName;
    private String avatarUrl;
}
