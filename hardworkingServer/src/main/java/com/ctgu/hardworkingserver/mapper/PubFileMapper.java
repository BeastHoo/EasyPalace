package com.ctgu.hardworkingserver.mapper;

import com.ctgu.hardworkingserver.entity.PubFile;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PubFileMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PubFile record);

    int insertSelective(PubFile record);

    PubFile selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PubFile record);

    List<PubFile> selectByUsername(String username);

    @MapKey("id")
    Map<Integer,PubFile> selectAll();

    List<PubFile> unionQuery(String username);

    int increaseCollectNum(Integer id);

    int increaseDownloadNum(Integer id);

    int reduceCollectNum(Integer id);

    List<PubFile> vagueQuery(String key);
}