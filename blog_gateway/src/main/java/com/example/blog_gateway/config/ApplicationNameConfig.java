package com.example.blog_gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 获取应用名称
 *
 * @author vains
 * @date 2021/4/5 2:44
 */
@Data
@Component
@ConfigurationProperties(prefix = ApplicationNameConfig.PREFIX)
public class ApplicationNameConfig {

    static final String PREFIX = "spring.application";

    /**
     * 应用名称
     */
    private String name;

}

