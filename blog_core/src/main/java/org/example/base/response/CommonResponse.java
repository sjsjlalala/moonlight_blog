package org.example.base.response;

import lombok.Data;
import org.example.base.enums.ErrorCode;

/*
 * @description:
 * @author: moki
 * @date: 2024/12/25 11:46
 * @param: 统一响应结果类
 * @return:
 **/
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

    public static  <T> CommonResponse<T> failure(Integer code, String message, T data) {
        return new CommonResponse<>(code, message, false, data);
    }
}
