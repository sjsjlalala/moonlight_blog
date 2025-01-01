package com.example.blog_web.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blog_web.entity.Tag;
import com.example.blog_web.mapper.TagMapper;
import com.example.blog_web.service.ITagService;
import com.example.blog_web.vo.TagVO;
import org.example.base.response.CommonResponse;
import org.example.base.uuid.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 标签 服务实现类
 * </p>
 *
 * @author moki
 * @since 2024-12-31
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {
    @Autowired
    private TagMapper tagMapper;
    /**
     * @description: 获取标签
     * @author: moki
     * @date: 2024/12/31 11:58
     * @param: []
     * @return: org.example.base.response.CommonResponse<com.example.blog_web.vo.TagVO>
     **/
    @Override
    public CommonResponse<List<TagVO>> getTags() {
        List<TagVO> tags = new ArrayList<>();
        // 找出所有父标签
        List<Tag> parentTags = tagMapper.selectList(new QueryWrapper<Tag>().isNull("parent_uid"));
        // 根据父标签找出对应的子标签
        for (Tag parentTag : parentTags) {
            List<Tag> childTags = tagMapper.selectList(new QueryWrapper<Tag>().eq("parent_uid", UUIDUtil.uuidToBytes(parentTag.getUid())));
            // 拼接VO
            tagsTranToVo(tags, parentTag, childTags);

        }
        return CommonResponse.success(tags);
    }

    private void tagsTranToVo(List<TagVO> tags, Tag parentTag, List<Tag> childTags) {
        TagVO tagVO = new TagVO();
        tagVO.setValue(parentTag.getUid());
        tagVO.setLabel(parentTag.getTagName());
        tagVO.setChildren(new ArrayList<>());
        for (Tag childTag : childTags) {
            TagVO childTagVO = new TagVO();
            childTagVO.setValue(childTag.getUid());
            childTagVO.setLabel(childTag.getTagName());
            tagVO.getChildren().add(childTagVO);
        }
        tags.add(tagVO);
    }
}
