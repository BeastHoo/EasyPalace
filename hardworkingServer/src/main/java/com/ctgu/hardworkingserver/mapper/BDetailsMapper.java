package com.ctgu.hardworkingserver.mapper;

import com.ctgu.hardworkingserver.entity.BDetails;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface BDetailsMapper {
    int deleteByPrimaryKey(String blogId);

    int insert(BDetails record);

    int insertSelective(BDetails record);

    BDetails selectByPrimaryKey(String blogId);

    int updateByPrimaryKeySelective(BDetails record);

    int updateByPrimaryKeyWithBLOBs(BDetails record);

    int updateByPrimaryKey(BDetails record);

    List<BDetails> selectAll();

    int updateClickRateByPrimaryKey(String bid);

    List<BDetails> selectByEditor(String editor);

    List<BDetails> vagueSearch(String key);
}