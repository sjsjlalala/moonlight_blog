package com.example.blog_web.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blog_web.entity.Tag;
import com.example.blog_web.mapper.TagMapper;
import com.example.blog_web.service.ITagService;
import com.example.blog_web.vo.OptionVO;
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
    public CommonResponse<List<OptionVO>> getTags() {
        List<OptionVO> tags = new ArrayList<>();
        // 找出所有父标签
        List<Tag> parentTags = tagMapper.selectList(new LambdaQueryWrapper<Tag>().isNull(Tag::getParentUid));
        // 根据父标签找出对应的子标签
        for (Tag parentTag : parentTags) {
            List<Tag> childTags = tagMapper.selectList(new LambdaQueryWrapper<Tag>().eq(Tag::getParentUid, UUIDUtil.uuidToBytes(parentTag.getUid())));
            // 拼接VO
            tagsTranToVo(tags, parentTag, childTags);

        }
        return CommonResponse.success(tags);
    }

    private void tagsTranToVo(List<OptionVO> tags, Tag parentTag, List<Tag> childTags) {
        OptionVO tagVO = new OptionVO();
        tagVO.setValue(parentTag.getUid());
        tagVO.setLabel(parentTag.getTagName());
        tagVO.setChildren(new ArrayList<>());
        for (Tag childTag : childTags) {
            OptionVO childTagVO = new  OptionVO();
            childTagVO.setValue(childTag.getUid());
            childTagVO.setLabel(childTag.getTagName());
            tagVO.getChildren().add(childTagVO);
        }
        tags.add(tagVO);
    }
}
