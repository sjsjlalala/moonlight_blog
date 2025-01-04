package com.example.blog_common.mybatisplus;

import org.apache.ibatis.type.TypeHandler;
import org.example.base.mybatisplus.MyMetaObjectHandler;
import org.example.base.mybatisplus.UuidToBinaryTypeHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: MyBatisPlus配置类，配置了自定义的类型处理类
 * @author: moki
 * @date: 2024/12/25 22:16
 **/
@Configuration
@MapperScan("com.example.blog_common.mapper")
public class MyBatisPlusConfig {


    @Bean
    public TypeHandler<String> uuidToBinaryTypeHandler() {
        return new UuidToBinaryTypeHandler();
    }
    /**
     * @description: 注册自定义元对象处理器
     * @author: moki
     * @date: 2024/12/26 20:17
     * @param: []
     * @return: org.example.base.mybatisplus.MyMetaObjectHandler
     **/
    @Bean
    public MyMetaObjectHandler myMetaObjectHandler() {
        return new MyMetaObjectHandler();
    }
}
