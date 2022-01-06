package com.ctgu.hardworkingserver.service;

import com.ctgu.hardworkingserver.entity.BTag;

import java.util.List;

public interface BTagService {
    int deleteByPrimaryKey(String tagId);

    int insert(BTag record);

    int insertSelective(BTag record);

    BTag selectByPrimaryKey(String tagId);

    int updateByPrimaryKeySelective(BTag record);

    int updateByPrimaryKey(BTag record);

    List<BTag> selectAll();
}
