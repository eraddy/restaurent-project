package com.epam.edai.run8.team11.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class RepositoryBodyDto<T> {
    private T data;
    private String message;
    private boolean success;
    private HttpStatus errorCode;

    // Static factory methods for common responses
    public static <T> RepositoryBodyDto<T> success(T data) {
        return RepositoryBodyDto.<T>builder()
                .data(data)
                .success(true)
                .message("Operation completed successfully")
                .build();
    }

    public static <T> RepositoryBodyDto<T> success(String message) {
        return RepositoryBodyDto.<T>builder()
                .data(null)
                .success(true)
                .message(message)
                .build();
    }

    public static <T> RepositoryBodyDto<T> success(T data, String message) {
        return RepositoryBodyDto.<T>builder()
                .data(data)
                .success(true)
                .message(message)
                .build();
    }

    public static <T> RepositoryBodyDto<T> error(String message) {
        return RepositoryBodyDto.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    public static <T> RepositoryBodyDto<T> error(String message, HttpStatus errorCode) {
        return RepositoryBodyDto.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }
}