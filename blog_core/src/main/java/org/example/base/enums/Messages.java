package org.example.base.enums;

/**
 * @description: 消息模板
 * @author: moki
 * @date: 2024/12/28 15:15
 **/
public interface Messages {
    /**
     * 类内部使用，自定义拒绝值消息
     */
    String CK_RANG_MESSAGE_LENGTH_TYPE = "长度必须在 0 到 11 之间：%s";
    String CK_NUMERIC_TYPE = "字段必须是数字：%s";

    /**
     * 注解默认消息
     */
    String CK_NOT_BLANK_DEFAULT = "不能为空";
    String CK_NOT_NULL_DEFAULT = "不能为空";
    String CK_NUMERIC_DEFAULT = "必须是数字";
    String CK_RANGE_DEFAULT = "应该是一个整数，在 {min} 和 {max} 之间";
    String ID_NOT_NULL = "不能为空";
    String PAGE_NOT_NULL = "页码不能为空";
    String SIZE_NOT_NULL = "大小不能为空";

    String ID_LENGTH_THIRTY_TWO = "长度必须是 32";


    /**
     * 文件上传消息
     */
    String FILE_UPLOAD_SUCCESS = "文件上传成功";
    String FILE_UPLOAD_FAIL = "文件上传失败";
    String FILE_NOT_EMPTY = "文件不能为空";
    String FILE_SIZE_TOO_LARGE = "文件大小超出限制: 不超过10MB";

    String FILE_TYPE_ERROR = "图片格式不支持";

    /**
     *  文件操作消息
     */
    String FILE_DELETE_SUCCESS = "文件删除成功";
    String FILE_DELETE_FAIL = "文件删除失败";
    String ADD_BLOG_SUCCESS = "博客添加成功";
}

