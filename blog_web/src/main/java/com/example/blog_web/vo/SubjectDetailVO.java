package com.example.blog_web.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.base.vo.BaseVO;

import java.util.List;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2025/1/13
 **/
@Data
@Setter
@Getter
public class SubjectDetailVO extends BaseVO<SubjectDetailVO> {
    private SubjectVO subject;
    private UserVO user;
    private List<BlogVO> blogs;

}
