package com.example.blog_web.vo;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
/**
 * @description: 前端博客请求封装
 * @author: moki
 * @date: 2025/1/1 17:33
 **/
@Data
@Setter
@Getter
public class BlogRequestVO {
    private BlogVO blogVO;
    private ArrayList<String> uuidsToDelete;
}