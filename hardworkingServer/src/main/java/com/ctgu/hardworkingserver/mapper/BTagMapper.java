package com.ctgu.hardworkingserver.mapper;

import com.ctgu.hardworkingserver.entity.BTag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BTagMapper {
    int deleteByPrimaryKey(String tagId);

    int insert(BTag record);

    int insertSelective(BTag record);

    BTag selectByPrimaryKey(String tagId);

    int updateByPrimaryKeySelective(BTag record);

    int updateByPrimaryKey(BTag record);

    List<BTag> selectAll();
}