package org.example.base.exception.customException;
/**
 * @description: api接口参数校验异常
 * @author: moki
 * @date: 2024/12/25 18:58
 * @param:
 * @return:
 **/
public class ApiInvalidParamException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ApiInvalidParamException() {
        super();
    }

    public ApiInvalidParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiInvalidParamException(String message) {
        super(message);
    }

    public ApiInvalidParamException(Throwable cause) {
        super(cause);
    }
}