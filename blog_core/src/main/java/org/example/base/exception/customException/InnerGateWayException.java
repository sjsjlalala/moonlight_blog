package org.example.base.exception.customException;

/**
 * @Description: 内部网关异常
 * @Author LiuMaoJi
 * @Date 2024/12/28
 **/
public class InnerGateWayException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    /**
     * 异常状态码
     */
    private int code;

    public InnerGateWayException() {
        super();
    }

    public InnerGateWayException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public InnerGateWayException(String message) {
        super(message);
    }

    public InnerGateWayException(Throwable cause) {
        super(cause);
    }
    public Integer getCode() {
        return code;
    }
}
