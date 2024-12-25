package com.example.blog_common.mybatisplus;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: MyBatisPlus配置类，配置了自定义的类型处理类
 * @author: moki
 * @date: 2024/12/25 22:16
 **/
@Configuration
@MapperScan("com.example.blog_common")
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

 /*   @Bean
    public TypeHandler<String> uuidToBinaryTypeHandler() {
        return new UuidToBinaryTypeHandler();
    }*/
}
