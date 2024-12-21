package com.example.blog_admin.restApi;




import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2024/12/21
 **/
@RestController
@RequestMapping("/admin")

@Slf4j
public class LoginApi {
    @PostMapping("/login")
    public String login(){
        return "欢迎login";
    }
}
