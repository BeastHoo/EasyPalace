package com.ctgu.hardworkingserver.service;

import com.ctgu.hardworkingserver.entity.BTag;
import com.ctgu.hardworkingserver.mapper.BTagMapper;
import com.ctgu.hardworkingserver.mapper.BlogMapper;
import com.ctgu.hardworkingserver.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BTagServiceImp implements BTagService{
    @Autowired
    private BTagMapper mapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public int deleteByPrimaryKey(String tagId) {
        return mapper.deleteByPrimaryKey(tagId);
    }

    @Override
    public int insert(BTag record) {
        return mapper.insert(record);
    }

    @Override
    public int insertSelective(BTag record) {
        return mapper.insertSelective(record);
    }

    @Override
    public BTag selectByPrimaryKey(String tagId) {
        return mapper.selectByPrimaryKey(tagId);
    }

    @Override
    public int updateByPrimaryKeySelective(BTag record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(BTag record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public List<BTag> selectAll() {
        ValueOperations<String,List> valueOperations = redisTemplate.opsForValue();
        List<BTag> list = valueOperations.get(Constant.BLOG_TAGS);

        if (list == null)
        {
            list = mapper.selectAll();
            valueOperations.set(Constant.BLOG_TAGS,list);
        }

        return list;
    }
}
