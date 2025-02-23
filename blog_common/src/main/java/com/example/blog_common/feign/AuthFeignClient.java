package com.example.blog_common.feign;

import org.example.base.response.CommonResponse;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2024/12/24
 **/
@ReactiveFeignClient(name = "blog-auth", url = "http://localhost:8082")
public interface AuthFeignClient {
    @RequestMapping(value = "/auth/validate", method = RequestMethod.GET)
    Mono<CommonResponse> validateToken(@RequestParam("token") String token);

    @RequestMapping(value = "/auth/logout", method = RequestMethod.POST)
    Mono<CommonResponse> logout(@RequestHeader("Authorization") String token);

    @RequestMapping(value = "/auth/validate-verification-code", method = RequestMethod.POST)
    Mono<CommonResponse> validateVerificationCode(@RequestHeader("Authorization") @RequestParam("email") String email, @RequestParam("code") String code);

}
