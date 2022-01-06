package com.ctgu.hardworkingserver.service;

import com.ctgu.hardworkingserver.entity.BDetails;
import com.ctgu.hardworkingserver.entity.Blog;

import java.util.List;

public interface BlogService {
    int deleteByPrimaryKey(String blogId);

    int insert(Blog record);

    int insertSelective(Blog record);

    Blog selectByPrimaryKey(String blogId);

    int updateByPrimaryKeySelective(Blog record);

    int updateByPrimaryKeyWithBLOBs(Blog record);

    int updateByPrimaryKey(Blog record);

}
