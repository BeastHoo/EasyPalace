package com.ctgu.hardworkingserver.service;

import com.ctgu.hardworkingserver.entity.CollectRecord;

import java.util.List;

public interface CollectService {
    int deleteRecord(Integer id,String username);

    int insert(CollectRecord record);

    int insertSelective(CollectRecord record);

    CollectRecord selectByPrimaryKey(Integer id);

    List<CollectRecord> selectByUsername(String username);

    int deleteByFid(Integer fid);

    List<CollectRecord> selectByPfid(Integer pfid);

    int updateByPrimaryKeySelective(CollectRecord record);

    int updateByPrimaryKey(CollectRecord record);
}
