package org.example.base.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.base.enums.EStatus;
import org.example.base.mybatisplus.UuidToBinaryTypeHandler;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * @Description Entity基类
 * @Author LiuMaoJi
 * @Date 2024/12/25
 **/

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseEntity<T extends Model<T>> extends Model<T> {
    /**
     *
     */
    private static final long serialVersionUID = -4851055162892178225L;

    /**
     * 唯一UID
     */
    @TableId(value = "uid", type = IdType.UUID)
    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String uid;

    /**
     * 状态 0：失效  1：生效
     */
    private int status;

    /**
     * @TableField 配置需要填充的字段
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDate createTime;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDate updateTime;

    public BaseEntity() {
        this.status = EStatus.ENABLE;
        this.createTime = LocalDate.now();
        this.updateTime = LocalDate.now();
    }

}
