package com.example.blog_auth.service;

import com.example.blog_auth.util.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Set;

/*
 * @description: 邮件服务
 * @author: moki
 * @date: 2025/2/21 10:40
 **/
@Service
public class EmailService {

    @Autowired
    private  TemplateEngine templateEngine;

    /**
     * 发送邮件
     */
    public boolean sendEmail(Set<String> emails, String title, String code) {
        return MailUtils.sendEmail(emails, title, generateRegistryEmailContent( code, 5));
    }

    /**
     * 生成动态 HTML 邮件内容
     */
    public String generateRegistryEmailContent( String code, int expiration) {
        // 创建 Thymeleaf 上下文对象
        Context context = new Context();
        context.setVariable("verificationCode", code);
        context.setVariable("expirationTime", expiration);

        // 渲染模板并返回 String
        return templateEngine.process("registry", context);
    }
}
