package com.sinchonthon.team3_backend.exception;

import com.sinchonthon.team3_backend.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    ResponseEntity<ApiResponse<Void>> api(ApiException e) {
        return ResponseEntity.status(e.status())
                .body(ApiResponse.error(e.status().value(), e.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> validation(MethodArgumentNotValidException e) {
        var errors = e.getBindingResult().getFieldErrors().stream()
                .map(x -> new ApiResponse.FieldError(x.getField(), x.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(ApiResponse.error(400, "요청 값이 올바르지 않습니다.", errors));
    }
}
