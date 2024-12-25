package com.example.blog_auth.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDate;
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

    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    private String username;

    private String password;

    private String email;

    private String phone;

    private Integer type;

    private String remarks;

    private Integer status;

    private LocalDate createTime;


    public Myuser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public Myuser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }


    @Override
    public String toString() {
        return userId + "," + username + "," + password + "," + email + "," + phone + "," + type + "," + remarks + "," + status + "," + createTime;
    }

    public static Myuser fromString(String userString) {
        String[] parts = userString.split(",");
        Myuser user = new Myuser(parts[1], parts[2], Collections.EMPTY_LIST);
        user.setUsername(parts[1]);
        user.setPassword(parts[2]);
        user.setUserId(Integer.parseInt(parts[0]));
        user.setEmail(parts[3]);
        user.setPhone(parts[4]);
        user.setType(Integer.parseInt(parts[5]));
        user.setRemarks(parts[6]);
        user.setStatus(Integer.parseInt(parts[7]));
        user.setCreateTime(LocalDate.parse(parts[8]));
        return user;
    }

}
