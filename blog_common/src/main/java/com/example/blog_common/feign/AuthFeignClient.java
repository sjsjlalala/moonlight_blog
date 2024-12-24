package com.example.blog_common.feign;

import com.example.blog_common.vo.CommonResponse;
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
    Mono<CommonResponse> validateToken(@RequestParam String token);
}
