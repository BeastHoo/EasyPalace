package com.ctgu.hardworkingserver.controller;


import com.ctgu.hardworkingserver.components.NonStaticResourceHttpRequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 已弃用
 */
@RestController
@Slf4j
public class PlayerController {

    @Autowired
    private  NonStaticResourceHttpRequestHandler nonStaticResourceHttpRequestHandler;

    @RequestMapping("/source")
    public void source(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam(name = "filename") String filename,
                       @RequestParam(name = "location")String location) throws Exception {


        //假如我把视频1.mp4放在了static下的video文件夹里面
        //sourcePath 是获取resources文件夹的绝对地址
        //realPath 即是视频所在的磁盘地址
//        String sourcePath = ClassUtils.getDefaultClassLoader().getResource("").getPath().substring(1);

        String realPath =  location+"/"+filename;

        log.info("realpath:"+realPath);
        Path filePath = Paths.get(realPath );
        if (Files.exists(filePath)) {
            log.info("已查找到该文件");
            String mimeType = Files.probeContentType(filePath);
            if (!StringUtils.isEmpty(mimeType)) {
                response.setContentType(mimeType);
            }
            request.setAttribute(NonStaticResourceHttpRequestHandler.ATTR_FILE, filePath);
            nonStaticResourceHttpRequestHandler.handleRequest(request, response);
        } else {
            log.info("未查找到该文件");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        }
    }
}
