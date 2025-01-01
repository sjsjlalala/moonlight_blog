package com.example.blog_web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blog_web.entity.Tag;
import com.example.blog_web.vo.TagVO;
import org.example.base.response.CommonResponse;

import java.util.List;

/**
 * <p>
 * 标签 服务类
 * </p>
 *
 * @author moki
 * @since 2024-12-31
 */
public interface ITagService extends IService<Tag> {

    CommonResponse<List<TagVO>> getTags();
}
