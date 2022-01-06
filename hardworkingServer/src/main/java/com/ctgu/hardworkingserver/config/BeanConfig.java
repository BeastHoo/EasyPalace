package com.ctgu.hardworkingserver.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.ctgu.hardworkingserver.entity.BDetails;
import com.ctgu.hardworkingserver.mapper.BDetailsMapper;
import com.ctgu.hardworkingserver.utils.Constant;
import com.ctgu.hardworkingserver.utils.JSONTransferUtil;
import com.ctgu.hardworkingserver.utils.Node;
import com.ctgu.hardworkingserver.utils.RankBinaryTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Configuration(proxyBeanMethods = true)
public class BeanConfig {

    /*
     * @auth BeastHoo
     * 初始化排序二叉树并将其注入spring容器
     */
    @Autowired
    private BDetailsMapper mapper;

    @Autowired
    private RedisTemplate redisTemplate;
    private static final String id = "" ;
    private static final String secret = "";

    @Bean
    OSS returnOss(){
        OSS ossClient = new OSSClientBuilder().build("https://oss-cn-chengdu.aliyuncs.com",id,secret);
        return ossClient;
    }

    @Bean
    Node rankBinaryTree(){
        initRedis();
        List<BDetails> bDetails = mapper.selectAll();
        Node root=new Node();

        Random random = new Random();

        root.setKey( random.nextInt(50));
        for (BDetails bDetail : bDetails) {
            Node node = new Node();
            node.setbDetail(bDetail);
            node.setValue(bDetail.getBlogId());
            node.setKey(bDetail.getClickRate());
            RankBinaryTree.Node_Insert(root,node);
        }

        return root;
    }

    void initRedis(){
        //这个map用来保存上传博客的图片资源路径(/username/blogid)和时间戳
        //目的是为了判断当用户向后台传了图片之后却没有上传博客，这个时候就把图片删了
        Map<String, String> map = new HashMap<>();
        ValueOperations<String, Map> opt = redisTemplate.opsForValue();
        opt.set(Constant.NEW_BLOG_ID,map);
    }

}
