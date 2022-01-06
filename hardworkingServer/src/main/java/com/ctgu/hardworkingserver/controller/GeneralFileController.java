package com.ctgu.hardworkingserver.controller;

import com.ctgu.hardworkingserver.entity.PubFile;
import com.ctgu.hardworkingserver.service.PrivateSpaceServiceImp;
import com.ctgu.hardworkingserver.service.PublicSpaceServiceImp;
import com.ctgu.hardworkingserver.utils.FeedBack;
import com.ctgu.hardworkingserver.utils.JSONTransferUtil;
import com.ctgu.hardworkingserver.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 公开文件controller
 * @auther BeastHoo
 */
@Slf4j
@RestController
public class GeneralFileController {
    private static Map<String,String> map;

    static {
        map= new HashMap<>();
        map.put(".mp4","v");
        map.put(".wav","m");
        map.put(".ogg","v");
        map.put(".mp3","m");
        map.put(".jpg","p");
        map.put(".png","p");
        map.put(".ico","p");
        map.put(".gif","p");
    }
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");

    @Autowired
    private PublicSpaceServiceImp publicSpaceService;

    @Autowired
    private PrivateSpaceServiceImp privateSpaceService;


    @RequestMapping("/uploads")
    public void handleFileUpload(HttpServletRequest request,HttpServletResponse response,
                                    @RequestParam(name = "mode")String mode,
                                    @RequestParam(name = "file")String fname,
                                    @RequestParam(name = "url")String url,
                                    @RequestParam(name = "size")String size) {   //注意参数

        log.info("mode:"+mode);
        String username = JwtUtil.getTokenInfo(request.getHeader("authorization")).getClaim("username").asString();
        FeedBack feedBack = new FeedBack();
        JSONTransferUtil.setResp(response);


            PubFile mFile=new PubFile();
            mFile.setLoc(url);
            // 检测是否存在目录


                try {
                    log.info("上传的文件名为：" + fname);//写日志
                    // 获取文件的后缀名
                    String suffixName = fname.substring(fname.lastIndexOf("."));
                    log.info("文件的后缀名为：" + suffixName);//写日志
                    if(map.containsKey(suffixName))
                    {
                        mFile.setIsplayable(true);
                        if(map.get(suffixName).equals("m"))
                        {
                            mFile.setIsmusic(true);
                            mFile.setIspic(false);
                            mFile.setIsvideo(false);
                        }else if (map.get(suffixName).equals("v")){
                            mFile.setIsvideo(true);
                            mFile.setIsmusic(false);
                            mFile.setIspic(false);
                        }else {
                            mFile.setIspic(true);
                            mFile.setIsvideo(false);
                            mFile.setIsmusic(false);
                        }
                    }
                    mFile.setCollectnum(0);
                    mFile.setDownloadnum(0);
                    mFile.setFilename(fname);
                    mFile.setUsername(username);
                    mFile.setMtime(simpleDateFormat.format(new Date()));
                    log.info(mFile.getMtime());
                    mFile.setMsize(getSize(size));
                    try {
                        if (mode.equals("pub"))
                        {
                            publicSpaceService.insert(mFile);
                        }else {
                            privateSpaceService.insert(mFile);
                        }
                    }catch (DuplicateKeyException e){
                        String filename = mFile.getFilename();

                        filename = filename.replace(".","("+new Random().nextInt(100)+").");
                        mFile.setFilename(filename);
                        if (mode.equals("pub"))
                        {
                            publicSpaceService.insert(mFile);
                        }else {
                            privateSpaceService.insert(mFile);
                        }
                    }


                } catch (Exception e) {
                    try {
                        e.printStackTrace();
                        feedBack.setStatus(0);
                        feedBack.setMessage("上传失败");
                        response.getWriter().write(JSONTransferUtil.transferBeanToJson(feedBack));
                        return;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
            }

        try {
            log.info("文件提交成功");
            feedBack.setStatus(1);
            feedBack.setMessage("上传成功");
            response.getWriter().write(JSONTransferUtil.transferBeanToJson(feedBack));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @RequestMapping("/download")
    public void downloadFile(HttpServletRequest request,
                             @RequestParam(name = "fid") Integer fid) {
        publicSpaceService.increase(fid,1);
    }


    private String getSize(String len) {
        String size = "";
        long fileLength = Long.parseLong(len);
        DecimalFormat df = new DecimalFormat("#.00");
        if (fileLength < 1024) {
            size = df.format((double) fileLength) + "BT";
        } else if (fileLength < 1048576) {
            size = df.format((double) fileLength / 1024) + "KB";
        } else if (fileLength < 1073741824) {
            size = df.format((double) fileLength / 1048576) + "MB";
        } else {
            size = df.format((double) fileLength / 1073741824) + "GB";
        }

        return size;

    }
}
