package com.example.blog_common.exceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.example.base.enums.ErrorCode;
import org.example.base.exception.customException.ApiInvalidParamException;
import org.example.base.exception.customException.InnerGateWayException;
import org.example.base.exception.customException.InsertException;
import org.example.base.response.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * @description:
 * @author: moki
 * @date: 2024/12/25 17:42
 * @param: 全局异常处理类
 * @return:
 **/
@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 通用插入失败异常处理
     */
    @ExceptionHandler(InsertException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Throwable> handleInsertFailedException(InsertException ex) {
        return CommonResponse.failure(ex.getCode(), ex.getMessage(), ex.getCause());
    }
    /**
     * 通用请求参数无效异常处理
     */
    @ExceptionHandler(ApiInvalidParamException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Throwable> requestParamInvalidException(ApiInvalidParamException ex) {
        log.error("ApiInvalidParamException caught", ex);
        return CommonResponse.failure(ErrorCode.REQUEST_PARAMS_ERROR.getCode(), ErrorCode.REQUEST_PARAMS_ERROR.getMessage(), ex.getCause());
    }
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleGlobalException(Exception ex) {
        log.error("Exception caught", ex);
        return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(InnerGateWayException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse<Throwable> In(InnerGateWayException ex) {
        return CommonResponse.failure(ex.getCode(), ex.getMessage(), ex.getCause());
    }
}
