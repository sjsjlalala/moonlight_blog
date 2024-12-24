package com.example.blog_common.vo;

import lombok.Data;

@Data
public class CommonResponse<T> {
    private Integer code;
    private String message;
    private T data;
    private Boolean success;

    public CommonResponse() {}

    public CommonResponse(Integer code, String message, Boolean success, T data) {
        this.code = code;
        this.message = message;
        this.success = success;
        this.data = data;
    }

    // 静态方法简化构建成功响应
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), true, data);
    }

    // 静态方法简化构建失败响应
    public static CommonResponse<Void> failure(Integer code, String message) {
        return new CommonResponse<>(code, message, false, null);
    }
}
