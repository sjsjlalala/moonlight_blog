package com.example.blog_web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blog_web.entity.Category;
import com.example.blog_web.mapper.CategoryMapper;
import com.example.blog_web.service.ICategoryService;
import com.example.blog_web.vo.OptionVO;
import org.example.base.response.CommonResponse;
import org.example.base.uuid.UUIDUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 博客分类 服务实现类
 * </p>
 *
 * @author moki
 * @since 2024-12-30
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {
    /**
     * @description: 获取系统分类
     * @author: moki
     * @date: 2025/1/12 12:48
     * @param: []
     * @return: org.example.base.response.CommonResponse<java.util.List < com.example.blog_web.vo.OptionVO>>
     **/
    @Override
    public CommonResponse<List<OptionVO>> getUserCategories() {
        // 先获取父分组
        List<OptionVO> res = this.baseMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getStatus, 1).isNull(Category::getParentUid)).stream().map(category -> {
            OptionVO optionVO = new OptionVO();
            optionVO.setValue(category.getUid());
            optionVO.setLabel(category.getCategoryName());
            optionVO.setChildren(dealWithCategory(optionVO));
            return optionVO;
        }).toList();
        return CommonResponse.success(res);
    }
    private List<OptionVO>dealWithCategory(OptionVO optionVO) {
        List<OptionVO> optionsVO = this.baseMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getStatus, 1).eq(Category::getParentUid, UUIDUtil.uuidToBytes(optionVO.getValue()))).stream().map(category -> {
            OptionVO optionVO1 = new OptionVO();
            optionVO1.setValue(category.getUid());
            optionVO1.setLabel(category.getCategoryName());
            optionVO1.setChildren(dealWithCategory(optionVO1));
            return optionVO1;}).toList();
        if (optionsVO.isEmpty()) {
            return null;
        } else {
            return optionsVO;
        }
        }

    }


    /**
     * @description: 获取用户自定义的分类
     * @author: moki
     * @date: 2025/1/1 11:29
     * @param: [userInfo]
     * @return: void
     **/


