package com.ctgu.hardworkingserver.service;

import com.ctgu.hardworkingserver.entity.Blog;
import com.ctgu.hardworkingserver.mapper.BlogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlogServiceImp implements BlogService{
    @Autowired
    private BlogMapper blogMapper;

    @Override
    public int deleteByPrimaryKey(String blogId) {
        return blogMapper.deleteByPrimaryKey(blogId);
    }

    @Override
    public int insert(Blog record) {
        return blogMapper.insert(record);
    }

    @Override
    public int insertSelective(Blog record) {
        return blogMapper.insertSelective(record);
    }

    @Override
    public Blog selectByPrimaryKey(String blogId) {
        return blogMapper.selectByPrimaryKey(blogId);
    }

    @Override
    public int updateByPrimaryKeySelective(Blog record) {
        return blogMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKeyWithBLOBs(Blog record) {
        return blogMapper.updateByPrimaryKeyWithBLOBs(record);
    }

    @Override
    public int updateByPrimaryKey(Blog record) {
        return blogMapper.updateByPrimaryKey(record);
    }
}
