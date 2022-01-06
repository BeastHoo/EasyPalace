package com.ctgu.hardworkingserver.security;


import com.ctgu.hardworkingserver.entity.User;
import com.ctgu.hardworkingserver.service.UserServiceImp;
import com.ctgu.hardworkingserver.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 自定义Realm
 * @author BeastHoo
 * date 2021/11/9
 */
@Slf4j
public class MyRealm extends AuthorizingRealm {

    @Autowired
    private UserServiceImp userService;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }
    /**
     * 授权
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /**
     * 认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) authenticationToken;
        String username = (String) jwtToken.getPrincipal();
//        log.info(principal);
//        DecodedJWT tokenInfo = JwtUtil.getTokenInfo(principal);
//        String username = tokenInfo.getClaim("username").asString();
        if (username == null)
            throw new UnknownAccountException();

        ValueOperations<String ,User> opt = redisTemplate.opsForValue();

        //先查redis再查数据库
        User user =opt.get(username);
        if (user == null)
        {
            user = userService.selectUserByName(username);
        }


        if(user == null)
            throw new UnknownAccountException();

        opt.set(username,user,30, TimeUnit.MINUTES);
        Map<String,String> map = new HashMap<>();
        map.put("username",user.getUsername());
        map.put("password",user.getPassword());
        if (! JwtUtil.verify(jwtToken.getToken(), map)) {
            throw new IncorrectCredentialsException();
        }

        //toke过期
        if(JwtUtil.isExpired(jwtToken.getToken())){
            throw new ExpiredCredentialsException();
        }
        log.info(user.toString());
        return new SimpleAuthenticationInfo(user.getUsername(),user.getPassword(), getName());
    }
}
