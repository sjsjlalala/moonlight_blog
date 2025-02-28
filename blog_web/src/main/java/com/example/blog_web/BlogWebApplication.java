package com.example.blog_web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactivefeign.spring.config.EnableReactiveFeignClients;
import springfox.documentation.oas.annotations.EnableOpenApi;


@SpringBootApplication(scanBasePackages = {"com.example.blog_web","com.example.blog_common"})
@MapperScan({"com.example.blog_web.mapper","com.example.blog_common"})
@EnableDiscoveryClient
@RefreshScope
@EnableOpenApi
@EnableReactiveFeignClients(basePackages = {"com.example.blog_common.feign"})
@EnableScheduling
public class BlogWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogWebApplication.class, args);
    }

}
