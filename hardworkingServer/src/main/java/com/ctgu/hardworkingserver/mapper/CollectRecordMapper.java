package com.ctgu.hardworkingserver.mapper;

import com.ctgu.hardworkingserver.entity.CollectRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CollectRecordMapper {
    int deleteRecord(Integer id,String username);

    int deleteByFid(Integer fid);

    int insert(CollectRecord record);

    int insertSelective(CollectRecord record);

    CollectRecord selectByPrimaryKey(Integer id);

    List<CollectRecord> selectByUsername(String username);

    List<CollectRecord> selectByPfid(Integer fid);

    int updateByPrimaryKeySelective(CollectRecord record);

    int updateByPrimaryKey(CollectRecord record);
}