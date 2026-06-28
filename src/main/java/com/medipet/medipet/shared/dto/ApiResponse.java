package com.medipet.medipet.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private ErrorBody error;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(true, null, null);
    }

    public static <T> ApiResponse<T> error(int status, String code, String message, String path) {
        ErrorBody error = new ErrorBody(status, code, message, path, LocalDateTime.now());
        return new ApiResponse<>(false, null, error);
    }

    @Getter
    @AllArgsConstructor
    public static class ErrorBody {
        private int status;
        private String code;
        private String message;
        private String path;
        private LocalDateTime timestamp;
    }
}
