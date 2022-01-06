package com.ctgu.hardworkingserver;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.ctgu.hardworkingserver.utils.JSONTransferUtil;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootTest
class HardworkingServerApplicationTests {

    @Test
    public void returnToken(HttpServletResponse res){
        DefaultProfile profile = DefaultProfile.getProfile("cn-chengdu", "LTAI5tQuLpSWb7n41s45ytxh", "GvAo23t8DisYsECCXQuZGxniBHtAp2");
        IAcsClient client = new DefaultAcsClient(profile);

        //鏋勯€犺姹傦紝璁剧疆鍙傛暟銆傚叧浜庡弬鏁板惈涔夊拰璁剧疆鏂规硶锛岃鍙傝API鍙傝€冦€�
        AssumeRoleRequest request = new AssumeRoleRequest();
        request.setRegionId("cn-chengdu");
        request.setRoleArn("acs:ram::1497092218857694:role/beasthoo");
        request.setRoleSessionName("nihao123");
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
