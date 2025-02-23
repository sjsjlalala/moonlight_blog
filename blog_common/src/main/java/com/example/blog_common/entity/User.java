package com.example.blog_common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.example.base.model.BaseEntity;
import org.example.base.mybatisplus.UuidToBinaryTypeHandler;

import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author moki
 * @since 2024-12-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
@ApiModel(value="User对象", description="")
public class User extends BaseEntity<User> {


    private String username;

    private String password;

    private String email;

    private String phone;

    private Integer type;

    private String remarks;
    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String avatarUid;


    public static User fromString(String userString) {
        try {
            String[] parts = userString.split(",");
            if (parts.length != 10) {
                throw new IllegalArgumentException("Invalid user string format");
            }

            User user = new User();
            user.setUid(parts[0]);
            user.setUsername(parts[1]);
            user.setPassword(parts[2]);
            user.setEmail(parts[3]);
            if (!parts[4].equals("null"))
            user.setPhone(parts[4]);
            if (!parts[5].equals("null"))
            user.setType(Integer.parseInt(parts[5]));
            if (!parts[6].equals("null"))
            user.setRemarks(parts[6]);
            if (!parts[7].equals("null"))
            user.setStatus(Integer.parseInt(parts[7]));

            user.setCreateTime(LocalDateTime.parse(parts[8]));
            user.setAvatarUid(parts[9]);

            return user;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error parsing user string: " + e.getMessage(), e);
        }
    }

}
