package com.example.blog_common.util;

import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description : token工具类
 * @Author LiuMaoJi
 * @Date 2024/12/28
 **/
public class TokenUtil {
    /**
     * @description: 请求头中获取token
     * @author: moki
     * @date: 2024/12/28 19:53
     * @param: [request]
     * @return: java.lang.String
     **/
    public static String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
