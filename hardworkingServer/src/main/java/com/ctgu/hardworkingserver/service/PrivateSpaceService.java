package com.ctgu.hardworkingserver.service;

import com.ctgu.hardworkingserver.entity.PubFile;

import java.util.List;
import java.util.Map;

public interface PrivateSpaceService {
    int deleteByPrimaryKey(Integer id);

    int insert(PubFile record);

    int insertSelective(PubFile record);

    PubFile selectByPrimaryKey(Integer id);

    List<PubFile> selectByUsername(String username);

    int updateByPrimaryKeySelective(PubFile record);

    int updateByPrimaryKey(PubFile record);

    List<PubFile> vagueQuery(String username,String key);
}
