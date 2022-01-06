package com.ctgu.hardworkingserver.security;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.ctgu.hardworkingserver.utils.JwtUtil;
import lombok.Data;
import lombok.val;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * 自定义的shiro接口token，通过本类将String类型的Token转换为AuthenticationToken供shiro使用
 * @author BeastHoo
 * Date 2021/11/9
 */

public class JwtToken implements AuthenticationToken {
    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        DecodedJWT tokenInfo = JwtUtil.getTokenInfo(token);
        return tokenInfo.getClaim("username").asString();
    }

    @Override
    public Object getCredentials() {
        DecodedJWT tokenInfo = JwtUtil.getTokenInfo(token);
        return tokenInfo.getClaim("password").asString();
    }
}
