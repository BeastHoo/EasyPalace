package com.ctgu.hardworkingserver.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ctgu.hardworkingserver.entity.CollectRecord;
import com.ctgu.hardworkingserver.entity.PubFile;
import com.ctgu.hardworkingserver.security.JwtToken;
import com.ctgu.hardworkingserver.service.CollectServiceImp;
import com.ctgu.hardworkingserver.service.PublicSpaceServiceImp;
import com.ctgu.hardworkingserver.utils.FeedBack;
import com.ctgu.hardworkingserver.utils.JSONTransferUtil;
import com.ctgu.hardworkingserver.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/pub")
public class PublicFileController {

    @Autowired
    private PublicSpaceServiceImp publicSpaceService;

    @Autowired
    private CollectServiceImp collectService;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private TransactionDefinition transactionDefinition;

    @RequestMapping("/files")
    public void findAllFiles(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("authorization");
        Map<Integer, PubFile> map = publicSpaceService.selectAll();
        JSONTransferUtil.setResp(response);
//        log.info(JSONTransferUtil.transferBeanToJsonArray(map.values()));
        //??????token?????????
        if (token != null && !token.equals(""))
        {
            try {
                //token??????????????????????????????
                SecurityUtils.getSubject().login(new JwtToken(token));
                DecodedJWT decodedJWT= JwtUtil.getTokenInfo(token);
                String username = decodedJWT.getClaim("username").asString();
                //??????????????????
                List<CollectRecord> list1 =  collectService.selectByUsername(username);

                //????????????????????????file???
                for (CollectRecord collectRecord : list1) {
                    map.get(collectRecord.getPfid()).setIscollected(true);
                }
            } catch (AuthenticationException e) {
                e.printStackTrace();
            }
        }
        try {
            response.getWriter().write(JSONTransferUtil.transferBeanToJsonArray(map.values()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //??????
    @RequestMapping("/collect")
    public void collect(@RequestParam(name = "fid") Integer id,
                        @RequestParam(name = "username") String fileowner,
                        HttpServletResponse response,
                        HttpServletRequest request)
    {
        log.info("====????????????====");
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
        String token = request.getHeader("authorization");
        JSONTransferUtil.setResp(response);

        DecodedJWT tokenInfo = JwtUtil.getTokenInfo(token);
        String username = tokenInfo.getClaim("username").asString();
        FeedBack feedBack = new FeedBack();

        if (username.equals(fileowner))
        {
            feedBack.setStatus(0);
            feedBack.setMessage("?????????????????????????????????!");
            try {
                response.getWriter().write(JSONTransferUtil.transferBeanToJson(feedBack));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
        CollectRecord record = new CollectRecord();
        record.setPfid(id);
        record.setUsername(username);

        try {
            collectService.insert(record);
            publicSpaceService.increase(id,0);
            feedBack.setStatus(1);
            feedBack.setMessage("????????????");

            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            e.printStackTrace();
            platformTransactionManager.rollback(transactionStatus);
            feedBack.setStatus(0);
            feedBack.setMessage("??????????????????,????????????");
        } finally {
            try {
                response.getWriter().write(JSONTransferUtil.transferBeanToJson(feedBack));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        }
    }


    @RequestMapping("/cancelcollect")
    public void cancelcollect(@RequestParam(name = "fid") Integer fid,
                              HttpServletResponse response,
                              HttpServletRequest request)
    {
        log.info("====????????????====");
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
        String token = request.getHeader("authorization");

        DecodedJWT tokenInfo = JwtUtil.getTokenInfo(token);
        String username = tokenInfo.getClaim("username").asString();

        FeedBack feedBack = new FeedBack();


        try {
            collectService.deleteRecord(fid,username);
            publicSpaceService.reduce(fid);
            feedBack.setStatus(1);
            feedBack.setMessage("??????????????????");

            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            e.printStackTrace();
            platformTransactionManager.rollback(transactionStatus);
            feedBack.setStatus(0);
            feedBack.setMessage("??????????????????,????????????");
        } finally {
            try {
                response.getWriter().write(JSONTransferUtil.transferBeanToJson(feedBack));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
