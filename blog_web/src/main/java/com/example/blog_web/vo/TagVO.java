package com.example.blog_web.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2024/12/31
 **/
@Data
@Getter
@Setter
public class TagVO {


    private String value;


    private String label;

    private List<TagVO> children;
}
