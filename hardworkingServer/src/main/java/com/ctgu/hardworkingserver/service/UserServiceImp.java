package com.ctgu.hardworkingserver.service;


import com.ctgu.hardworkingserver.entity.User;
import com.ctgu.hardworkingserver.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class UserServiceImp implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public User selectUserByName(String name) {
        ValueOperations<String,User> opt=redisTemplate.opsForValue();
        User user = opt.get(name);
        if (user == null)
        {
            user=userMapper.selectByPrimaryKey(name);
        }
        if (user != null)
        {
            opt.set(name,user,30, TimeUnit.MINUTES);
        }
        return user;
    }

    @Override
    public int insert(User user) {
        return userMapper.insert(user);
    }

    @Override
    public int update(User user) {
        ValueOperations<String,User> opt=redisTemplate.opsForValue();
        opt.set(user.getUsername(),user);
        return userMapper.updateByPrimaryKey(user);
    }

    public int register(User user){
        //加密 ： MD5 + salt + 1024次散列
        String uuId = UUID.randomUUID().toString();
        String code = uuId.substring(uuId.length()-6);
        user.setSalt(code);
        Md5Hash md5Hash = new Md5Hash(user.getPassword(),user.getSalt(),1024);
        user.setPassword(md5Hash.toHex());
        //Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
        user.setRegisttime(simpleDateFormat.format(new Date()));
        log.info(user.toString());
        //注入数据库
        return insert(user);
    }

    @Override
    public int findout(String name) {
        User user = selectUserByName(name);
        if (user != null)
        {
            return 1;
        }
        return 0;
    }

    @Override
    public int findEmail(String email) {
//        User user = userMapper.selectUserByEmail(email);
        return userMapper.selectUserByEmail(email) == null ? 1:0;
    }

    @Override
    public int updateUserInfo(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }
}
