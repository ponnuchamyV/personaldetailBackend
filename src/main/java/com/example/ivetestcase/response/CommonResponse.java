package com.example.ivetestcase.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonResponse<T> {

    private Boolean isError;
    private String message;
    private T data;

    public static <T> CommonResponse<T> success(String message, T data) {
        return CommonResponse.<T>builder()
                .isError(false)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> CommonResponse<T> failure(String message) {
        return CommonResponse.<T>builder()
                .isError(true)
                .message(message)
                .data(null)
                .build();
    }

    public static <T> CommonResponse<T> failure(String message, T data) {
        return CommonResponse.<T>builder()
                .isError(true)
                .message(message)
                .data(data)
                .build();
    }


}

