package org.example.base.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.base.enums.ErrorCode;

/**
 * @description: 统一响应结果类
 * @author: moki
 * @date: 2024/12/28 16:52
 **/
@Data
@Getter
@Setter
public class CommonResponse<T> {
    private Integer code;
    private String message;
    private Boolean success;
    private T data;
    private Integer currentPage;
    private Integer pageSize;
    private Long totalRecords;
    private Integer totalPages;



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
    // 静态方法简化构建成功响应，包含分页信息
    public static <T> CommonResponse<T> success(T data, int currentPage, int pageSize, long totalRecords, int totalPages) {
        CommonResponse<T> response = new CommonResponse<>();
        response.setCode(ErrorCode.SUCCESS.getCode());
        response.setMessage(ErrorCode.SUCCESS.getMessage());
        response.setData(data);
        response.setCurrentPage(currentPage);
        response.setPageSize(pageSize);
        response.setTotalRecords(totalRecords);
        response.setTotalPages(totalPages);
        return response;
    }

    // 静态方法简化构建失败响应
    public static CommonResponse<Void> failure(Integer code, String message) {
        return new CommonResponse<>(code, message, false, null);
    }

    public static  <T> CommonResponse<T> failure(Integer code, String message, T data) {
        return new CommonResponse<>(code, message, false, data);
    }
}
