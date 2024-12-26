package com.example.blog_common.service.impl;

import com.example.blog_common.entity.Tag;
import com.example.blog_common.mapper.TagMapper;
import com.example.blog_common.service.ITagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 标签 服务实现类
 * </p>
 *
 * @author moki
 * @since 2024-12-26
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {

}
