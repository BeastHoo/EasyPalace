package com.ctgu.hardworkingserver.service;


import com.ctgu.hardworkingserver.entity.Msg;
import com.ctgu.hardworkingserver.mapper.MsgMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MsgServiceImp implements MsgService{
    @Autowired
    MsgMapper msgMapper;

    @Override
    public List<Msg> selectRecMsgByUsername(String username) {
        return msgMapper.selectByTo(username);
    }

    @Override
    public int deleteById(int id) {
        return msgMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int updateStatus(int id) {
        return msgMapper.updateStatus(id);
    }

    @Override
    public int createNewMsg(Msg msg) {
        return msgMapper.insert(msg);
    }
}
