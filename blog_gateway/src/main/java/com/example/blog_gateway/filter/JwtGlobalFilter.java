package com.example.blog_gateway.filter;



import com.example.blog_common.feign.AdminFeignClient;
import com.example.blog_common.feign.AuthFeignClient;
import com.example.blog_common.vo.CommonResponse;
import com.example.blog_gateway.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Component
public class JwtGlobalFilter implements GlobalFilter, Ordered {

    @Lazy
    @Resource
    private AuthFeignClient authFeignClient;
    private static final Logger logger = LoggerFactory.getLogger(JwtGlobalFilter.class);


    @Value("${gateway.whitelists}")
    private  List<String> WHITELISTED_IPS;



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        logger.info("JwtGlobalFilter 处理请求: {}", request.getURI());
        String path = request.getURI().getPath();
        if (path.matches(".*swagger.*")) {
            logger.info("请求来自 swagger.");
            return chain.filter(exchange);
        }
        if (WHITELISTED_IPS.contains(path)) {
            logger.info("请求来自白名单 IP: {}", path);
            return chain.filter(exchange);
        }
        String token = resolveToken(request);

        if (token != null ) {
            Mono<CommonResponse> commonResponseMono = authFeignClient.validateToken(token);
            if (Objects.requireNonNull(commonResponseMono.block()).getCode() == 200) {
                logger.info("token校验通过");

            }
            else {
                logger.warn(Objects.requireNonNull(commonResponseMono.block()).getMessage());
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                // 将错误信息写入响应体
                byte[] bytes = Objects.requireNonNull(commonResponseMono.block()).getMessage().getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                return response.writeWith(Mono.just(buffer));

            }
        }
        return chain.filter(exchange);

    }

    private String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }



    @Override
    public int getOrder() {
        // 设置过滤器的优先级，数值越小优先级越高
        return -100;
    }
}
