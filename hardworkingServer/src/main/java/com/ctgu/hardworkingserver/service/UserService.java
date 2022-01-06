package com.ctgu.hardworkingserver.service;

import com.ctgu.hardworkingserver.entity.User;

public interface UserService {
    User selectUserByName(String name);
    int insert(User user);
    int update(User user);
    int register(User user);
    int findout(String name);
    int findEmail(String email);

    int updateUserInfo(User user);
}
