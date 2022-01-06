package com.ctgu.hardworkingserver.service;

import com.ctgu.hardworkingserver.entity.BDetails;
import com.ctgu.hardworkingserver.mapper.BDetailsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogDetailServiceImp implements BlogDetailService {
    @Autowired
    private BDetailsMapper mapper;

    @Override
    public int deleteByPrimaryKey(String blogId) {
        return mapper.deleteByPrimaryKey(blogId);
    }


    @Override
    public int insert(BDetails record) {
        return mapper.insert(record);
    }

    @Override
    public int insertSelective(BDetails record) {
        return mapper.insertSelective(record);
    }

    @Override
    public BDetails selectByPrimaryKey(String blogId) {
        return mapper.selectByPrimaryKey(blogId);
    }

    @Override
    public int updateByPrimaryKeySelective(BDetails record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKeyWithBLOBs(BDetails record) {
        return mapper.updateByPrimaryKeyWithBLOBs(record);
    }

    @Override
    public int updateByPrimaryKey(BDetails record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public List<BDetails> selectAll() {
        return mapper.selectAll();
    }

    @Override
    public int updateClickRateByPrimaryKey(String bid) {
        return mapper.updateClickRateByPrimaryKey(bid);
    }

    @Override
    public List<BDetails> selectByEditor(String editor) {
        return mapper.selectByEditor(editor);
    }


    @Override
    public List<BDetails> vagueSearch(String key) {
        return mapper.vagueSearch(key);
    }
}
