package com.example.blog_file;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import reactivefeign.spring.config.EnableReactiveFeignClients;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication(scanBasePackages = {"com.example.blog_file","com.example.blog_common"})
@MapperScan({"com.example.blog_file.mapper","com.example.blog_common.mapper"})
@EnableDiscoveryClient
@RefreshScope
@EnableOpenApi
@EnableReactiveFeignClients(basePackages = {"com.example.blog_common.feign"})
public class BlogFileApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogFileApplication.class, args);
	}

}
