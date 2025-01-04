package com.example.blog_web.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.example.base.vo.BaseVO;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2024/12/31
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Getter
@Setter
public class TagVO extends BaseVO<TagVO> {

    private String tagName;

    private String description;

    private String parentUid;

    private Long clicks;

    private Integer sort;
}
