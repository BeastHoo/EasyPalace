package com.ctgu.hardworkingserver.mapper;

import com.ctgu.hardworkingserver.entity.PubFile;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PriFileMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PubFile record);

    int insertSelective(PubFile record);

    PubFile selectByPrimaryKey(Integer id);

    List<PubFile> selectByUsername(String username);

    int updateByPrimaryKeySelective(PubFile record);

    int updateByPrimaryKey(PubFile record);

    List<PubFile> vagueQuery(String username,String key);
}