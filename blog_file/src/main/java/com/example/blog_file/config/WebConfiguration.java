package com.example.blog_file.config;

import com.example.blog_common.util.CommonUserRedisUtil;
import com.example.blog_file.Interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Value("${file.image.uploadPath}")
    private String uploadPath;
    @Value("${file.image.visualMappingPathPattern}")
    private String visualMappingPath;
    @Autowired
    private CommonUserRedisUtil commonUserRedisUtil;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor(commonUserRedisUtil))
                // 拦截的请求
                .addPathPatterns("/upload/**")
                // 排除swagger3路径
                .excludePathPatterns();

    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/upload-files/**") //虚拟url路径
                .addResourceLocations("file:C:/Users/27038/Desktop/GraduationProject/uploadFiles/"); //真实本地路径
    }
}