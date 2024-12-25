package com.example.blog_common.dto;

import lombok.Data;
import org.example.base.validtor.annotion.NotBlank;
import org.example.base.validtor.group.Common;

@Data
public class AuthRequest {
    @NotBlank(groups = {Common.class})
    private String username;
    @NotBlank(groups = {Common.class})
    private String password;
}
