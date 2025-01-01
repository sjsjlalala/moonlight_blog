package com.example.blog_web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blog_common.entity.User;
import com.example.blog_web.entity.UserCategory;
import com.example.blog_web.mapper.UserCategoryMapper;
import com.example.blog_web.service.IUserCategoryService;
import com.example.blog_web.vo.OptionVO;
import org.example.base.enums.EStatus;
import org.example.base.response.CommonResponse;
import org.example.base.uuid.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 博客用户自定义分类 服务实现类
 * </p>
 *
 * @author moki
 * @since 2025-01-01
 */
@Service
public class UserCategoryServiceImpl extends ServiceImpl<UserCategoryMapper, UserCategory> implements IUserCategoryService {
    @Autowired
    private UserCategoryMapper userCategoryMapper;
    @Override
    public CommonResponse<List<OptionVO>> getUserCategories(User userInfo) {
        List<OptionVO> optionsVO = new ArrayList<>();
        // 根据用户uid找出所有有效顶级分组
        List<UserCategory> userCategories = userCategoryMapper.selectList(
                new LambdaQueryWrapper<UserCategory>()
                        .eq(UserCategory::getUserUid, UUIDUtil.uuidToBytes(userInfo.getUid()))
                        .eq(UserCategory::getStatus, EStatus.VALID)
                        .isNull(UserCategory::getParentUid)
        );
        // 递归获取所有层级的分类
        userCategories.forEach(userCategory -> {
            OptionVO optionVO = new OptionVO();
            DtoToVORecursive(userCategory, optionVO);
            optionsVO.add(optionVO);
        });
        return CommonResponse.success(optionsVO);
    }




    private void DtoToVORecursive(UserCategory userCategory, OptionVO optionVO) {
        optionVO.setValue(userCategory.getUid());
        optionVO.setLabel(userCategory.getCategoryName());
        // 找出子分类
        List<UserCategory> childCategories = userCategoryMapper.selectList(
                new LambdaQueryWrapper<UserCategory>()
                        .eq(UserCategory::getParentUid, UUIDUtil.uuidToBytes(userCategory.getUid()))
                        .eq(UserCategory::getStatus, EStatus.VALID)
        );
        if (!childCategories.isEmpty()) {
            List<OptionVO> children = new ArrayList<>();
            childCategories.forEach(childCategory -> {
                OptionVO childOptionVO = new OptionVO();
                DtoToVORecursive(childCategory, childOptionVO);
                children.add(childOptionVO);
            });
            optionVO.setChildren(children);
        }
    }

}
