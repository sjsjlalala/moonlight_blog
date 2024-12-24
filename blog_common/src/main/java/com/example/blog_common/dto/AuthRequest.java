package com.example.blog_common.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
