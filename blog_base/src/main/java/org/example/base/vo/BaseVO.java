package org.example.base.vo;


import lombok.Data;
import org.example.base.validtor.group.Delete;
import org.example.base.validtor.group.Update;

/**
 * @description:
 * @author: moki
 * @date: 2024/12/25 16:31
 * @param: VO基类
 * @return: 
 **/
@Data
public class BaseVO<T> extends PageInfo<T> {

    /**
     * 唯一UID
     */
    private String uid;

    private Integer status;
}
