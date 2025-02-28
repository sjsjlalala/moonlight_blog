package com.example.blog_web.config;

import com.example.blog_common.util.CommonUserRedisUtil;
import com.example.blog_web.Interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Autowired
    private CommonUserRedisUtil commonUserRedisUtil;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor(commonUserRedisUtil))
                // 拦截的请求
                .addPathPatterns("/**")
                // 排除swagger3路径
                .excludePathPatterns(
                        "/v3/api-docs",
                        "/blog/blogList",
                        "/blog/blogDetailByUid/**",
                        "/tag/getTags",
                        "/blog/fetchBlogListByTag/**",
                        "/subject/fetchSubjectDetail",
                        "/category/fetchSysCategory",
                        "/subject/fetchSubjectDetail",
                        "/comment/getCommentByBlogUid");

    }

}