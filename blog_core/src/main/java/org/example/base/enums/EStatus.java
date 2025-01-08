package org.example.base.enums;

/*
 * @description:
 * @author: moki
 * @date: 2024/12/25 15:03
 * @param: 状态枚举类
 * @return:
 **/
public class EStatus {
    /**
     * 删除的
     */
    public static final int DISABLED = 0;
    /**
     * 激活的
     */
    public static final int ENABLE = 1;
    /**
     * 冻结的
     */
    public static final int FREEZE = 2;
    /**
     * 置顶的
     */
    public static final int STICK = 3;
    /**
     * 有效的
     */
    public static final int VALID = 1;
    /**
     * 无效的
     **/
    public static final int INVALID = 0;
    /**
     * 根组件
     **/
    public static final String isRoot = "isRoot";
    /**
     * 博客文件夹
     **/
    public static final int BLOG_FOLDER = 1;
    /**
     * 博客收藏夹
     **/
    public static final int BLOG_COLLECTION = 2;

}
