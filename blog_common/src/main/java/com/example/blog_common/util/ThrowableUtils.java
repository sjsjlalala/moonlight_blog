package com.example.blog_common.util;

import cn.hutool.core.collection.CollectionUtil;
import org.example.base.constants.Constants;
import org.example.base.enums.ErrorCode;
import org.example.base.exception.customException.ApiInvalidParamException;
import org.example.base.exception.customException.InnerGateWayException;
import org.example.base.exception.customException.InsertException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: moki
 * @date: 2024/12/25 17:48
 * @param: 异常抛出类
 * @return:
 **/
public class ThrowableUtils {
    /**
     * 通用插入异常
     */
    public static InsertException insertException(InsertException ex) {
        return ex;
    }
    /**
     * API接口请求参数校验
     */
    public static void checkApiParams(BindingResult result) {
        if (result != null && result.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> errors = result.getFieldErrors();
            if (CollectionUtil.isNotEmpty(errors)) {
                FieldError error = errors.get(0);
                String rejectedValue = Objects.toString(error.getRejectedValue(), "");
                String defMsg = error.getDefaultMessage();
                // 排除类上面的注解提示
                if (rejectedValue.contains(Constants.DELIMITER_TO)) {
                    // 自己去确定错误字段
                    sb.append(defMsg);
                } else {
                    if (Constants.DELIMITER_COLON.contains(defMsg)) {
                        sb.append(error.getField()).append(" ").append(defMsg);
                    } else {
                        sb.append(error.getField()).append(" ").append(defMsg);
                    }
                }
            } else {
                String msg = result.getAllErrors().get(0).getDefaultMessage();
                sb.append(msg);
            }
            throw new ApiInvalidParamException(sb.toString());
        }
    }

    public static String checkToken(HttpServletRequest request) {
        String token = TokenUtil.resolveToken(request);
        if (token == null || token.isEmpty()) {
            throw new InnerGateWayException(ErrorCode.INNER_GATEWAY_ERROR.getMessage() + "异常请求路径： " + request.getRequestURI(), null, ErrorCode.INNER_GATEWAY_ERROR.getCode());
        }
        return token;
    }

    /**
     * API接口请求参数校验,校验容器
     */
    public static void checkList(Collection<String> uuids) {
        if (CollectionUtil.isEmpty(uuids)) {
            throw new ApiInvalidParamException("请选择要删除的数据");
        }
    }
}
