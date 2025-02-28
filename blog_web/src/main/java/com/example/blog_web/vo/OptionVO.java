package com.example.blog_web.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description: 前端级联选择器
 * @Author LiuMaoJi
 * @Date 2025/1/1
 **/
@Data
@Getter
@Setter
public class OptionVO {
    private String value;

    private String label;

    private List<OptionVO> children;
}
