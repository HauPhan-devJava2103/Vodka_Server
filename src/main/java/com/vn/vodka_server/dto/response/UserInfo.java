package com.vn.vodka_server.dto.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vn.vodka_server.util.EGender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfo {
    private Long id;
    private String fullName;
    @JsonFormat(pattern = "MM/dd/yyyy", timezone = "Asia/Ho_Chi_Minh")
    private Date dateOfBirth;
    private EGender gender;
    private String phone;
    private String email;
    private String avatarUrl;
    private String provider;
}
