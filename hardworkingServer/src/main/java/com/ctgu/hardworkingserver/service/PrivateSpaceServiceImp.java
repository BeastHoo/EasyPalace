package com.ctgu.hardworkingserver.service;


import com.ctgu.hardworkingserver.entity.PubFile;
import com.ctgu.hardworkingserver.mapper.PriFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PrivateSpaceServiceImp implements PrivateSpaceService{
    @Autowired
    private PriFileMapper priFileMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return priFileMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(PubFile record) {
        return priFileMapper.insert(record);
    }

    @Override
    public int insertSelective(PubFile record) {
        return priFileMapper.insertSelective(record);
    }

    @Override
    public PubFile selectByPrimaryKey(Integer id) {
        return priFileMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<PubFile> selectByUsername(String username) {
        return priFileMapper.selectByUsername(username);
    }

    @Override
    public int updateByPrimaryKeySelective(PubFile record) {
        return priFileMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(PubFile record) {
        return priFileMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<PubFile> vagueQuery(String username, String key) {
        return priFileMapper.vagueQuery(username,key);
    }
}
