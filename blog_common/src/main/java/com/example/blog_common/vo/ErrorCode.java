package com.example.blog_common.vo;

public enum ErrorCode {
    // 成功
    SUCCESS(200, "操作成功"),
    
    // 系统错误
    SYSTEM_ERROR(500, "系统内部错误"),
    INVALID_REQUEST(400, "请求参数无效"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源未找到"),
    
    // 用户相关错误
    USER_NOT_FOUND(40401, "用户不存在"),
    USER_DISABLED(40301, "用户已被禁用,请联系管理员"),
    USER_ALREADY_EXISTS(40901, "用户已存在"),
    INVALID_CREDENTIALS(40101, "用户名或密码错误"),
    
    // JWT 相关错误
    INVALID_TOKEN(40102, "无效的令牌"),
    EXPIRED_TOKEN(40103, "令牌已过期"),
    TOKEN_VERIFICATION_FAILED(40104, "令牌验证失败"),
    
    // 其他业务逻辑错误
    DUPLICATE_RECORD(409, "记录已存在"),
    DATA_INTEGRITY_VIOLATION(40902, "数据完整性约束违反"),
    OPERATION_FAILED(50001, "操作失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
