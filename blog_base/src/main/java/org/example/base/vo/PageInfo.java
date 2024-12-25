package org.example.base.vo;


import lombok.Data;


/**
 * @description:
 * @author: moki
 * @date: 2024/12/25 16:40
 * @param: 分页
 * @return: 
 **/
@Data
public class PageInfo<T> {

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 当前页
     */

    private Long currentPage;

    /**
     * 页大小
     */

    private Long pageSize;
}
