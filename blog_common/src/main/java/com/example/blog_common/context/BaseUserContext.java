package com.example.blog_common.context;

import com.example.blog_common.entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BaseUserContext {
    protected final static ThreadLocal<User> baseContext = new ThreadLocal<>();

    /**
     * @description: 请求结束后释放线程变量
     * @author: moki
     * @date: 2024/12/28 19:27
     * @param: []
     * @return: void
     **/
    public static void clear() {
        BaseUserContext.baseContext.remove();

    }
}
