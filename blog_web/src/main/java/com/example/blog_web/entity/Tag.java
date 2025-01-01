package com.example.blog_web.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.example.base.model.BaseEntity;
import org.example.base.mybatisplus.UuidToBinaryTypeHandler;

/**
 * <p>
 * 标签
 * </p>
 *
 * @author moki
 * @since 2024-12-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tag")
@ApiModel(value="Tag对象", description="标签")
public class Tag extends BaseEntity<Tag> {


    @ApiModelProperty(value = "标签名字")
    private String tagName;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "父标签")
    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String parentUid;

    @ApiModelProperty(value = "点击数")
    private Long clicks;

    @ApiModelProperty(value = "排序")
    private Integer sort;




}
