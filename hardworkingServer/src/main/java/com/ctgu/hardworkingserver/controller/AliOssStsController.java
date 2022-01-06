package com.ctgu.hardworkingserver.controller;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.ctgu.hardworkingserver.utils.JSONTransferUtil;
import com.ctgu.hardworkingserver.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Slf4j
public class AliOssStsController {
    private static final String appKeyId = "";
    private static final String appKeySecrete = "";

    @RequestMapping("/getToken")
    private void returnToken(HttpServletRequest req, HttpServletResponse res){

        DefaultProfile profile = DefaultProfile.getProfile("cn-chengdu", appKeyId, appKeySecrete);
        IAcsClient client = new DefaultAcsClient(profile);
        String username = JwtUtil.getTokenInfo(req.getHeader("authorization")).getClaim("username").asString();
        log.info(username + "：申请阿里OSS STS临时凭证...");
        AssumeRoleRequest request = new AssumeRoleRequest();
        request.setRegionId("cn-chengdu");
        request.setRoleArn("acs:ram::1497092218857694:role/beasthoo");
        request.setRoleSessionName(username);
        request.setDurationSeconds((long) (60*30));


        try {
            AssumeRoleResponse response = client.getAcsResponse(request);
            JSONTransferUtil.setResp(res);
            res.getWriter().write(JSONTransferUtil.transferBeanToJson(response.getCredentials()));
        } catch (ServerException | IOException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
    }
}
