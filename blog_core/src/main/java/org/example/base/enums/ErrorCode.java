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
    REQUEST_PARAMS_NOT_MULTIPART(50013, "请求参数不是Multipart格式"),

    // 内部错误
    INNER_GATEWAY_ERROR(50001, "内部网关错误,传入了空token"),
    INNER_GATEWAY_TIMEOUT(50002, "内部网关超时"),
    INNER_GATEWAY_SERVICE_UNAVAILABLE(50003, "内部网关服务不可用"),
    INNER_GATEWAY_BAD_GATEWAY(50004, "内部网关错误"),
    INNER_GATEWAY_NOT_FOUND(50005, "内部网关错误"),

    // 文件错误
    FILE_DELETE_FAILED(50006, "文件删除失败"),

    // 评论
    COMMENT_CONTENT_NULL(50007, "评论内容不能为空"),
    SUBMIT_COMMENT_FAILED(50008, "评论失败"),
    COMMENT_NOT_EXIST(50009, "评论不存在"),
    // 博客
    BLOG_NOT_EXIST(50010, "博客不存在"),
    BLOG_ALREADY_COLLECTED(50011, "博客已经收藏"),

    CREATE_COLLECTION_FAILED (50012, "创建收藏夹失败"),
    COLLECT_FAILED(50013, "博客收藏失败"),
    CANCEL_COLLECT_FAILED(50014, "取消博客收藏失败" ),
    // 分组
    CREATE_CATEGORY_FAILED(50015, "创建分组失败"),
    FILE_NOT_FOUND(50016, "文件不存在"),
    // 专题
    CREATE_SUBJECT_FAILED(50017, "创建专题失败"),
    FETCH_USER_SUBJECT_FAILED(50018,"" );
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
