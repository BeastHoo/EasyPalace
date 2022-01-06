package com.ctgu.hardworkingserver.service;

import com.ctgu.hardworkingserver.entity.Msg;

import java.util.List;

public interface MsgService {
    List<Msg> selectRecMsgByUsername(String username);//从数据库中获取已经收到的信息
    int deleteById(int id);
    int updateStatus(int id);
    int createNewMsg(Msg msg);
}
