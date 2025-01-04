package org.example.base.vo;


import lombok.Data;

/**
 * @description: VO基类,主要用于支持分页
 * @author: moki
 * @date: 2024/12/25 16:31
 **/
@Data
public class BaseVO<T>  {

    /**
     * 唯一UID
     */
    private String uid;

    private Integer status = 1 ;

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 当前页
     */

    private Integer currentPage;

    /**
     * 页大小
     */

    private Integer pageSize;
}
