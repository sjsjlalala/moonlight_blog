package com.example.blog_auth.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2025/2/21
 **/
@Getter
@Setter
public class Email {
    private Set<String> emails;
    private String title;
    private String content;
}
