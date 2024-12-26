package org.example.base.enums;

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
    Blog_ADD_FAILED(50001, "添加博客失败"),
    
    // JWT 相关错误
    INVALID_TOKEN(40102, "无效的令牌"),
    EXPIRED_TOKEN(40103, "令牌已过期"),
    TOKEN_VERIFICATION_FAILED(40104, "令牌验证失败"),
    
    // 其他业务逻辑错误
    DUPLICATE_RECORD(409, "记录已存在"),
    DATA_INTEGRITY_VIOLATION(40902, "数据完整性约束违反"),
    OPERATION_FAILED(50001, "操作失败"),
    INSERT_FAILED(50001, "插入数据失败"),
    UPDATE_FAILED(50002, "更新数据异常"),
    DELETE_FAILED(50003, "删除数据失败"),
    SELECT_FAILED(50004, "查询数据失败"),
    EXECUTE_FAILED(50005, "执行SQL失败"),
    PARAM_EXCEPTION(50006, "参数异常"),
    FILE_EXCEPTION(50007, "文件异常"),
    REQUEST_PARAMS_ERROR(50008, "请求参数错误"),
    REQUEST_PARAMS_NULL(50009, "请求参数为空"),
    REQUEST_PARAMS_NOT_JSON(50010, "请求参数不是JSON格式"),
    REQUEST_PARAMS_NOT_XML(50011, "请求参数不是XML格式"),
    REQUEST_PARAMS_NOT_FORM(50012, "请求参数不是表单格式"),
    REQUEST_PARAMS_NOT_MULTIPART(50013, "请求参数不是Multipart格式");


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
