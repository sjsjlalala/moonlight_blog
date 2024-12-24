package com.example.blog_common.feign;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "blog-admin", url = "http://localhost:8080")
public interface AdminFeignClient {
    @RequestMapping(value = "/admin/login", method = RequestMethod.GET)
    Mono<String> login();
}
