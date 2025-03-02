package com.example.blog_common.AOP;

import com.example.blog_common.entity.OperationLog;
import com.example.blog_common.service.IOperationLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Optional;

@Aspect
@Component
public class OperationLogAspect {

    private final IOperationLogService logService;

    public OperationLogAspect(IOperationLogService logService) {
        this.logService = logService;
    }

    @Around("@annotation(org.example.base.annotation.OperationLog)")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        OperationLog log = new OperationLog();
        log.setCreateTime(LocalDateTime.now());

        try {
            // 1. 获取注解信息
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            org.example.base.annotation.OperationLog annotation =
                    method.getAnnotation(org.example.base.annotation.OperationLog.class);

            // 2. 填充注解信息
            log.setModule(annotation.module());
            log.setType(annotation.type());
            log.setDescription(annotation.value());
            log.setMethod(getFullMethodName(joinPoint));

            // 3. 获取请求上下文
            Optional<HttpServletRequest> request = getCurrentRequest();
            request.ifPresent(req -> {
                log.setIp(getClientIp(req));
            });

            // 4. 获取用户信息
            getCurrentUser().ifPresent(user -> {
                log.setUserId(user.getUserId());
                log.setUsername(user.getUsername());
            });

            // 5. 执行目标方法
            Object result = joinPoint.proceed();
            log.setStatus(1);
            return result;
        } catch (Exception e) {
            log.setStatus(0);
            throw e;
        } finally {
            // 6. 保存日志（同步）
            logService.save(log);
        }
    }

    // 获取完整方法名（类名#方法名）
    private String getFullMethodName(ProceedingJoinPoint joinPoint) {
        return joinPoint.getSignature().getDeclaringTypeName() + "#" +
                joinPoint.getSignature().getName();
    }

    // 获取当前请求（安全方式）
    private Optional<HttpServletRequest> getCurrentRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(attrs -> attrs instanceof ServletRequestAttributes)
                .map(attrs -> ((ServletRequestAttributes) attrs).getRequest());
    }

    // 获取客户端IP（增强版）
    private String getClientIp(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (isValidIp(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    // 验证IP有效性
    private boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip);
    }

    // 获取当前用户（示例实现，根据实际项目调整）
    private Optional<CurrentUser> getCurrentUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof CurrentUser)
                .map(principal -> (CurrentUser) principal);
    }

    // 当前用户示例类（根据实际项目实现）
    public static class CurrentUser {
        private String userId;
        private String username;

        // getters...
    }
}