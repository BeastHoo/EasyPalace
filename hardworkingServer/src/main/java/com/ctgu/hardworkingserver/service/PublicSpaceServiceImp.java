package com.ctgu.hardworkingserver.service;

import com.ctgu.hardworkingserver.entity.PubFile;
import com.ctgu.hardworkingserver.mapper.PubFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PublicSpaceServiceImp implements PublicSpaceService{
    @Autowired
    private PubFileMapper pubFileMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return pubFileMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(PubFile record) {
        return pubFileMapper.insert(record);
    }

    @Override
    public int insertSelective(PubFile record) {
        return pubFileMapper.insertSelective(record);
    }

    @Override
    public PubFile selectByPrimaryKey(Integer id) {
        return pubFileMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(PubFile record) {
        return pubFileMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<PubFile> selectByUsername(String username) {
        return pubFileMapper.selectByUsername(username);
    }

    @Override
    public Map<Integer, PubFile> selectAll() {
        return pubFileMapper.selectAll();
    }


    /**
     * flag==0,collectnum++;flag==1,downloadnum++
     * @param record
     * @param flag
     * @return
     */
    @Override
    public int increase(Integer record, int flag) {
        if (flag == 0)
            return pubFileMapper.increaseCollectNum(record);
        else
            return pubFileMapper.increaseDownloadNum(record);
    }


    /**
     * @param record
     * @return
     */
    public int reduce(Integer record) {
        return pubFileMapper.reduceCollectNum(record);
    }

    public List<PubFile> unionQuery(String username){
        return pubFileMapper.unionQuery(username);
    }

    @Override
    public List<PubFile> vagueQuery(String key) {
        return pubFileMapper.vagueQuery(key);
    }
}
