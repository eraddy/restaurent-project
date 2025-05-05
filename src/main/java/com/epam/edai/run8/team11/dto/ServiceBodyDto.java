package com.epam.edai.run8.team11.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ServiceBodyDto<T> {
    private T data;
    private String message;
    private boolean success;
    private HttpStatus errorCode;

    // Static factory methods for common responses
    public static <T> ServiceBodyDto<T> success(T data) {
        return ServiceBodyDto.<T>builder()
                .data(data)
                .success(true)
                .message("Operation completed successfully")
                .build();
    }

    public static <T> ServiceBodyDto<T> success(String message) {
        return ServiceBodyDto.<T>builder()
                .data(null)
                .success(true)
                .message(message)
                .build();
    }

    public static <T> ServiceBodyDto<T> success(T data, String message) {
        return ServiceBodyDto.<T>builder()
                .data(data)
                .success(true)
                .message(message)
                .build();
    }

    public static <T> ServiceBodyDto<T> error(String message) {
        return ServiceBodyDto.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    public static <T> ServiceBodyDto<T> error(String message, HttpStatus errorCode) {
        return ServiceBodyDto.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }
}