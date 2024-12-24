package com.example.blog_auth.security;

import com.example.blog_auth.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 配置Spring Security的安全策略，包括用户认证、HTTP安全配置和会话管理。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyUserDetailsService userDetailsService; // 自动注入用户详情服务

    /**
     * 配置用户认证管理器，使用自定义的用户详情服务和BCrypt密码加密器。
     *
     * @param auth 认证管理器构建器
     * @throws Exception 如果配置过程中发生异常
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        // 使用MyUserDetailsService提供用户详情，并使用BCryptPasswordEncoder进行密码加密
    }

    /**
     * 配置HTTP安全策略，禁用CSRF保护，允许所有用户访问`/authenticate`路径，
     * 其他请求需要认证。会话管理设置为无状态（STATELESS）。
     *
     * @param http HTTP安全配置构建器
     * @throws Exception 如果配置过程中发生异常
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable() // 禁用CSRF保护，适用于无状态API
                .authorizeRequests()
                .antMatchers("/auth/login", "/auth/**",  "/swagger-ui/index.html/swagger-resources", "/swagger-ui/**","/swagger-resources/**","/swagger-ui/**", "/v3/**", "/error")
                .permitAll() // 允许所有用户访问`/auth/login`路径
                .anyRequest().authenticated() // 所有其他请求都需要认证
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 会话管理设置为无状态
    }
    @Override
    public void configure(WebSecurity web){
        web.ignoring().antMatchers("/auth/login", "/auth/**",  "/swagger-ui/index.html/swagger-resources", "/swagger-ui/**","/swagger-resources/**","/swagger-ui/**", "/v3/**", "/error");
    }

    /**
     * 暴露AuthenticationManager Bean供外部调用。
     *
     * @return AuthenticationManager实例
     * @throws Exception 如果获取过程中发生异常
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 创建并返回一个BCryptPasswordEncoder实例，用于密码加密。
     *
     * @return PasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 使用BCrypt算法加密密码
    }
}
