package com.example.blog_common.feign;

import org.example.base.response.CommonResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

/**
 * @Description: 文件feign客户端
 * @Author LiuMaoJi
 * @Date 2025/1/1
 **/
@ReactiveFeignClient(name = "blog-file", url = "http://localhost:8084")
public interface FileFeignClient {
    @RequestMapping(value = "/file/deleteFile", method = RequestMethod.POST)
    Mono<CommonResponse> deleteFile(@RequestParam("uuids") ArrayList<String> uuids);

    @RequestMapping(value = "/fetchFile/fileDetail", method = RequestMethod.GET)
    Mono<CommonResponse> getFileDetailById(@RequestParam("uid") String uid);

    @RequestMapping(value = "/fetchFile/fileUrlById", method = RequestMethod.GET)
    Mono<CommonResponse> getFileUrlById(@RequestParam("uid") String uid);
}
