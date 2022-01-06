package com.ctgu.hardworkingserver.controller;


import com.ctgu.hardworkingserver.entity.CollectRecord;
import com.ctgu.hardworkingserver.entity.Msg;
import com.ctgu.hardworkingserver.entity.PubFile;
import com.ctgu.hardworkingserver.service.*;
import com.ctgu.hardworkingserver.utils.FeedBack;
import com.ctgu.hardworkingserver.utils.JSONTransferUtil;
import com.ctgu.hardworkingserver.utils.JwtUtil;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/pri")
public class PrivateFileController {

    @Autowired
    private PrivateSpaceServiceImp privateSpaceService;

    @Autowired
    private PublicSpaceServiceImp publicSpaceService;

    @Autowired
    private CollectServiceImp collectService;

    @Autowired
    private MsgService msgService;

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");

    @RequestMapping("/file")
    public void query(HttpServletRequest request, HttpServletResponse response){
        String username = JwtUtil.getTokenInfo(request.getHeader("authorization")).getClaim("username").asString();

        //1.查找privatefiles表中的数据
        List<PubFile> priList = privateSpaceService.selectByUsername(username);
        for (PubFile pubFile : priList) {
            pubFile.setType(true);
        }
        //2.根据收藏记录查找publicfiles中的数据
        List<PubFile> colList = publicSpaceService.unionQuery(username);
        List<PubFile> pubList = publicSpaceService.selectByUsername(username);
        for (PubFile pubFile : colList) {
            pubFile.setIscollected(true);
        }

        //3.将两种数据结合，发送给前端
        priList.addAll(colList);
        priList.addAll(pubList);
//        log.info(pubList.toString());
//        log.info(JSONTransferUtil.transferBeanToJsonArray(priList));
        JSONTransferUtil.setResp(response);
        try {
            response.getWriter().write(JSONTransferUtil.transferBeanToJsonArray(priList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Transactional
    @RequestMapping("/del")
    public void delete(HttpServletResponse response,
                       @RequestParam(name = "fid") Integer fid,
                       @RequestParam(name = "filename") String filename,
                       @RequestParam(name = "type")Boolean type)
    {
        log.info("删除******");
        FeedBack feedBack = new FeedBack();
        JSONTransferUtil.setResp(response);

        int res = 0;
        if (type)
            res = privateSpaceService.deleteByPrimaryKey(fid);
        else {
            res = publicSpaceService.deleteByPrimaryKey(fid);
            if (res != 0)
            {
                List<CollectRecord> list= collectService.selectByPfid(fid);
                collectService.deleteByFid(fid);
                for (CollectRecord collectRecord : list) {
                    Msg msg = new Msg();
                    msg.setUserto(collectRecord.getUsername());
                    msg.setContent("您收藏的文件: "+filename+" 已经被发布者删除!");
                    msg.setMtime(simpleDateFormat.format(new Date()));
                    msg.setTitle("系统通知");
                    msg.setUserfrom("admin");
                    msg.setStatus(0);
                    msgService.createNewMsg(msg);
                }
            }
        }

        if (res==0)
        {
            feedBack.setMessage("删除失败!");
            feedBack.setStatus(0);
        }else
        {
            feedBack.setMessage("删除成功!");
            feedBack.setStatus(1);
        }

        try {
            response.getWriter().write(JSONTransferUtil.transferBeanToJson(feedBack));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
