package com.vn.vodka_server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.vn.vodka_server.dto.response.ApiResponse;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({ MethodArgumentNotValidException.class,
            ConstraintViolationException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleValidationException(Exception e, WebRequest request) {
        ApiResponse errorResponse = new ApiResponse();

        // Xu ly message lỗi để trả về client
        String message = e.getMessage();
        if (e instanceof MethodArgumentNotValidException) {
            int start = message.lastIndexOf("[");
            int end = message.lastIndexOf("]");
            message = message.substring(start + 1, end - 1);
            errorResponse.setMessage(message);

        } else if (e instanceof ConstraintViolationException) {
            int start = message.lastIndexOf(":");
            message = message.substring(start + 1).trim();
            errorResponse.setMessage(message);
        }
        return errorResponse;

    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse handleInternalServerErrorException(Exception e, WebRequest request) {
        ApiResponse errorResponse = new ApiResponse();

        if (e instanceof MethodArgumentTypeMismatchException) {
            String message = e.getMessage();
            int start = message.lastIndexOf("Failed to convert value of type");
            if (start != -1) {
                message = message.substring(start);
                errorResponse.setMessage(message);
            } else {
                errorResponse.setMessage("Internal Server Error");
            }
        } else {
            errorResponse.setMessage("Internal Server Error");
        }
        return errorResponse;

    }

    // 1. Lỗi 404: Tài nguyên không tồn tại
    // Khi Service ném ra ResourceNotFoundException, handler này sẽ tự động
    // chuyển nó thành JSON lỗi với status 404, Controller không cần try-catch
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.builder()
                        .success(false)
                        .message(e.getMessage())
                        .build());
    }

    // 2. Lỗi 400: Dữ liệu khách gửi lên sai định dạng
    // Khi Service ném ra BadRequestException, handler này sẽ tự động chuyển nó
    // thành JSON lỗi với status 400, Controller không cần try-catch
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse> handleBadRequest(BadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .success(false)
                        .message(e.getMessage())
                        .build());
    }

    // 3. Lỗi 500: Bắt tất cả lỗi chưa được xử lý
    // Nếu có bất kỳ lỗi nào khác mà không có handler riêng, nó sẽ rơi vào đây
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.builder()
                        .success(false)
                        .message("Hệ thống đang bận, vui lòng thử lại sau!")
                        .build());
    }
}
