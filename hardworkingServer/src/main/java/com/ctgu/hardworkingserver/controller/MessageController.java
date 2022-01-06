package com.ctgu.hardworkingserver.controller;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.ctgu.hardworkingserver.entity.Msg;
import com.ctgu.hardworkingserver.service.MsgServiceImp;
import com.ctgu.hardworkingserver.utils.FeedBack;
import com.ctgu.hardworkingserver.utils.JSONTransferUtil;
import com.ctgu.hardworkingserver.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 * 消息控制器
 * @author BeastHoo
 */
@RestController
@Slf4j
public class MessageController {

    /**
     * 备忘录：暂时还缺少消息回复功能
     * 2021/11/11
     */

    @Autowired
    MsgServiceImp msgService;

    //消息查询
    @RequestMapping("/msg")
    public void getMessage(HttpServletResponse response, HttpServletRequest request){
        String token = request.getHeader("authorization");//获取token
        DecodedJWT tokenInfo = JwtUtil.getTokenInfo(token);
        String username = tokenInfo.getClaim("username").asString();
        List<Msg> msgs = msgService.selectRecMsgByUsername(username);
        String s = JSONTransferUtil.transferBeanToJsonArray(msgs);
        JSONTransferUtil.setResp(response);
        FeedBack feedBack = new FeedBack();
        feedBack.setStatus(1);
        feedBack.setMessage("查询成功!");

        try {
            response.getWriter().write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //状态更新
    @RequestMapping("/updateStatus")
    public void updateStatus(HttpServletResponse response,Integer id)
    {
        int cnt = msgService.updateStatus(id);
        FeedBack feedBack = new FeedBack();
        JSONTransferUtil.setResp(response);

        if (cnt == 0)
        {
            feedBack.setStatus(0);
            feedBack.setMessage("更新状态失败");
            log.info("更新消息状态失败，消息id:=>"+id);
        }
        else {
            feedBack.setStatus(1);
            feedBack.setMessage("更新状态成功");
            log.info("更新消息状态成功，消息id:=>"+id);
        }

        try {
            response.getWriter().write(JSONTransferUtil.transferBeanToJson(feedBack));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
