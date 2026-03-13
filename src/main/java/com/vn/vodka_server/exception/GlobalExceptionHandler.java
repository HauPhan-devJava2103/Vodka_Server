package com.vn.vodka_server.exception;

import org.springframework.http.HttpStatus;
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
}
