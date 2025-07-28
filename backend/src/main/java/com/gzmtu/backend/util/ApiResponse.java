package com.gzmtu.backend.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<Void>(200, "success", null); // 👈 显式指定 Void
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public static ApiResponse<Void> error(int code, String message, boolean noData) {
        return new ApiResponse<Void>(code, message, null); // 👈 同样需要指定 Void
    }
}
