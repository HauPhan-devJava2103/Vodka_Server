package com.vn.vodka_server.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

// Custom Exception cho lỗi "không tìm thấy dữ liệu"
// Khi Service không tìm thấy phim, review, genre... sẽ ném ra lỗi này
// GlobalExceptionHandler sẽ tự động bắt và trả về JSON lỗi với HTTP 404
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
