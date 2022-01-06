package com.ctgu.hardworkingserver.service;

import com.ctgu.hardworkingserver.entity.PubFile;

import java.util.List;
import java.util.Map;

public interface PublicSpaceService {
    int deleteByPrimaryKey(Integer id);

    int insert(PubFile record);

    int insertSelective(PubFile record);

    PubFile selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PubFile record);

    List<PubFile> selectByUsername(String username);

    Map<Integer, PubFile> selectAll();

    int increase(Integer record, int flag);

    List<PubFile> vagueQuery(String key);
}
