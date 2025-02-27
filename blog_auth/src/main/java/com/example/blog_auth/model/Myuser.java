package com.example.blog_auth.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.example.base.mybatisplus.UuidToBinaryTypeHandler;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;


/**
 * <p>
 * 
 * </p>
 *
 * @author author
 * @since 2024-12-24
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
@ApiModel(value="User对象", description="")
public class Myuser  extends User implements UserDetails{

    private static final long serialVersionUID = 1L;

    @TableId(value = "uid", type = IdType.ASSIGN_UUID)
    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String uid;

    private String username;

    private String password;

    private String email;

    private String phone;

    private Integer type;

    private String remarks;

    private Integer status;

    private String avatarUid;
    private String avatarUrl;
    private LocalDateTime createTime;

    public Myuser() {
        super("", "", Collections.EMPTY_LIST);

    }
    public Myuser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public Myuser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }


    @Override
    public String toString() {
        return uid + "," + username + "," + password + "," + email + "," + phone + "," + type + "," + remarks + "," + status + "," + createTime + "," + avatarUid;
    }

    public static Myuser fromString(String userString) {
        String[] parts = userString.split(",");
        Myuser user = new Myuser(parts[1], parts[2], Collections.EMPTY_LIST);
        user.setUsername(parts[1]);
        user.setPassword(parts[2]);
        user.setUid(parts[0]);
        user.setEmail(parts[3]);
        if (!parts[4].equals("null"))
        user.setPhone(parts[4]);
        if (!parts[5].equals("null"))
        user.setType(Integer.parseInt(parts[5]));
        if (!parts[6] .equals("null"))
        user.setRemarks(parts[6]);
        if (!parts[7].equals("null"))
        user.setStatus(Integer.parseInt(parts[7]));
        user.setCreateTime(LocalDateTime.parse(parts[8]));
        user.setAvatarUid(parts[9]);
        return user;
    }

}
