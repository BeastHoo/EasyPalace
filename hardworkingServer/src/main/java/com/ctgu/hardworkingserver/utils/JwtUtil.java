package com.ctgu.hardworkingserver.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * jwt工具类
 * @author BeastHoo
 * Date 2021/11/9
 */
@Slf4j
public class JwtUtil {
    private static final String signature="*b#ea@sth$oo";

    /**
     * 生成token header.payload.sign
     * @return
     */
    public static String getToken(Map<String,String> map)
    {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE,1);//默认一天过期

        JWTCreator.Builder builder = JWT.create();

        //payload
        map.forEach(builder::withClaim);

        String token = builder.withExpiresAt(instance.getTime()).sign(Algorithm.HMAC256(signature));
        return token;
    }


    /**
     * 验证合法性
     * @param token
     * @param map
     * @return
     */
    public static boolean verify(String token,Map<String,String>map){
        try {
            log.info(map.toString());
            //根据密钥生成JWT效验器
            Algorithm algorithm = Algorithm.HMAC256(signature);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", map.get("username"))
                    .withClaim("password", map.get("password"))
                    .build();
            //效验TOKEN（其实也就是比较两个token是否相同）
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 获取token信息
     * @param token
     * @return
     */
    public static DecodedJWT getTokenInfo(String token)
    {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(signature)).build().verify(token);
        return decodedJWT;
    }

    public static boolean isExpired(String token)
    {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC256(signature)).build().verify(token);
        return jwt.getExpiresAt().getTime() < System.currentTimeMillis();
    }



}
