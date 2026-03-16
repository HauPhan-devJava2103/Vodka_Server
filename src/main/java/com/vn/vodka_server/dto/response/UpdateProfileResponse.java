package com.vn.vodka_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateProfileResponse {
    private UserInfo updatedUser;
}
