package org.example.base.exception.customException;

import org.example.base.enums.ErrorCode;

import java.io.Serializable;

/**
 * @description:
 * @author: moki
 * @date: 2024/12/25 17:31
 * @param: 自定义插入异常
 * @return:
 **/
public class InsertException extends RuntimeException implements Serializable {

    /**
     * 异常状态码
     */
    private int code;

    public InsertException() {
        super(ErrorCode.INSERT_FAILED.getMessage());
        this.code = ErrorCode.INSERT_FAILED.getCode();
    }

    public InsertException(String message, Throwable cause) {
        super(message, cause);
        this.code =  ErrorCode.INSERT_FAILED.getCode();
    }

    public InsertException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public InsertException(String message) {
        super(message);
        this.code = ErrorCode.INSERT_FAILED.getCode();;
    }

    public InsertException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public InsertException(Throwable cause) {
        super(cause);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
