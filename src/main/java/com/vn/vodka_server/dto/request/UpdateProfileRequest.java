package com.vn.vodka_server.dto.request;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vn.vodka_server.util.EGender;
import com.vn.vodka_server.util.EStatus;
import com.vn.vodka_server.validator.EnumValue;
import com.vn.vodka_server.validator.PhoneNumber;
import com.vn.vodka_server.validator.StrongPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "Tên hiển thị không được để trống")
    private String displayName;

    @NotNull(message = "dateOfBirth must be not null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date dateOfBirth;

    @NotBlank(message = "Số điện thoại không được để trống")
    @PhoneNumber(message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotNull(message = "Giới tính không được để trống")
    @EnumValue(enumClass = EGender.class)
    private EGender gender;

    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @StrongPassword
    private String password;

    private String avatarUrl;

    @NotNull(message = "Trạng thái không được để trống")
    @EnumValue(enumClass = EStatus.class)
    private EStatus status;

}
