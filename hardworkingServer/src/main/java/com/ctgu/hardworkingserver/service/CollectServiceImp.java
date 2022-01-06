package com.ctgu.hardworkingserver.service;

import com.ctgu.hardworkingserver.entity.CollectRecord;
import com.ctgu.hardworkingserver.mapper.CollectRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectServiceImp implements CollectService{
    @Autowired
    private CollectRecordMapper collectRecordMapper;

    @Override
    public int deleteRecord(Integer id,String username) {
        return collectRecordMapper.deleteRecord(id,username);
    }

    @Override
    public int insert(CollectRecord record) {
        return collectRecordMapper.insert(record);
    }

    @Override
    public int insertSelective(CollectRecord record) {
        return collectRecordMapper.insertSelective(record);
    }

    @Override
    public CollectRecord selectByPrimaryKey(Integer id) {
        return collectRecordMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<CollectRecord> selectByUsername(String username) {
        return collectRecordMapper.selectByUsername(username);
    }

    @Override
    public int updateByPrimaryKeySelective(CollectRecord record) {
        return collectRecordMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int deleteByFid(Integer fid){
        return collectRecordMapper.deleteByFid(fid);
    }

    @Override
    public List<CollectRecord> selectByPfid(Integer pfid) {
        return collectRecordMapper.selectByPfid(pfid);
    }

    @Override
    public int updateByPrimaryKey(CollectRecord record) {
        return collectRecordMapper.updateByPrimaryKey(record);
    }
}
