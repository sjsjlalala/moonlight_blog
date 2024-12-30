package com.example.blog_file.response;

import lombok.Data;
import org.example.base.enums.Messages;

/**
 * @Description wangEditor图片上传响应类
 * @Author LiuMaoJi
 * @Date 2024/12/28
 **/
@Data
public class ImageResponse {
    private int errno;
    private Data data;
    private String message;

    public ImageResponse() {
    }
    public ImageResponse(int errno, Data data, String message) {
        this.errno = errno;
        this.data = data;
        this.message = message;
    }
    public static class Data {
        private String url; // 图片 src，必须
        private String alt; // 图片描述文字，非必须
        private String href; // 图片的链接，非必须

        // Getter 和 Setter 方法
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getAlt() {
            return alt;
        }

        public void setAlt(String alt) {
            this.alt = alt;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }
    }
    public static ImageResponse success(String url, String alt, String href) {
        Data data = new Data();
        data.url = url;
        data.alt = alt;
        data.href = href;

        return new ImageResponse(0, data, Messages.FILE_UPLOAD_SUCCESS);
    }
    public static ImageResponse failure() {
        return new ImageResponse(1, null, Messages.FILE_UPLOAD_FAIL);
    }
    public static ImageResponse failure(String message)  {
        return new ImageResponse(1, null, message);
    }
}
