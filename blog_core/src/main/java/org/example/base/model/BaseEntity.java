package org.example.base.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.base.enums.EStatus;
import org.example.base.mybatisplus.UuidToBinaryTypeHandler;

import java.time.LocalDateTime;

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
    @TableField(typeHandler = UuidToBinaryTypeHandler.class, fill = FieldFill.INSERT)
    private String uid;

    /**
     * 状态 0：失效  1：生效
     */
    private int status = 1;

    /**
     * @TableField 配置需要填充的字段
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;

    public BaseEntity() {
        this.status = EStatus.ENABLE;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

}
