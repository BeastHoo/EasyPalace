package com.ctgu.hardworkingserver.controller;

import com.aliyun.oss.OSS;
import com.ctgu.hardworkingserver.entity.BDetails;
import com.ctgu.hardworkingserver.entity.BTag;
import com.ctgu.hardworkingserver.entity.Blog;
import com.ctgu.hardworkingserver.service.BTagServiceImp;
import com.ctgu.hardworkingserver.service.BlogDetailServiceImp;
import com.ctgu.hardworkingserver.service.BlogServiceImp;
import com.ctgu.hardworkingserver.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/myblog")
public class MyblogController {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:MM:ss");

    @Autowired
    public BlogServiceImp blogService;

    @Autowired
    HomePageController homePageController;

    @Autowired
    public RedisTemplate redisTemplate;

    @Autowired
    public BlogDetailServiceImp blogDetailService;

    @Autowired
    public BTagServiceImp bTagService;

    @Autowired
    OSS ossClient;

    @RequestMapping("/writebegin")
    public void handleFileUpload(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam(name = "blogId")String blogId) {   //注意参数
        ValueOperations<String,Map> opt = redisTemplate.opsForValue();
        Map<String,String> map = opt.get(Constant.NEW_BLOG_ID);
        String username = JwtUtil.getTokenInfo(request.getHeader("authorization")).getClaim("username").asString();
        assert map != null;
        map.put(username+"/blog/"+blogId,simpleDateFormat.format(new Date()));
        opt.set(Constant.NEW_BLOG_ID,map);
        FeedBack feedBack = new FeedBack();
        JSONTransferUtil.setResp(response);
        try {
            String s = JSONTransferUtil.transferBeanToJson(feedBack);
            response.getWriter().write(s);
            log.info(username+":开始写博客");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
     *已弃用
    @RequestMapping("/delImg")
    private void delImg(HttpServletRequest request,HttpServletResponse response,
                        @RequestParam(name = "imgUrl") String imgUrl){
        log.info("url: "+imgUrl);

        String[] split = imgUrl.split("&");
        String[] uParam = split[0].split("=");
        String username = uParam[1];
        String[] fParam = split[1].split("=");
        String filename = fParam[1];

        log.info(imgUrl);
        log.info(username);
        log.info(filename);
        String filepath = "./FILE/BLOG/"+ username + "/"+filename;
        File file = new File(filepath);
        boolean flag = false;
        if (file.exists())
        {
            flag = file.delete();
        }
        FeedBack feedBack = new FeedBack();
        JSONTransferUtil.setResp(response);
        log.info(filepath);
        if (flag)
        {
            feedBack.setStatus(1);
            feedBack.setMessage("删除成功!");
        }
        else {
            feedBack.setStatus(0);
            feedBack.setMessage("删除失败!未找到文件!");
        }

        try {
            response.getWriter().write(JSONTransferUtil.transferBeanToJson(feedBack));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @RequestMapping("/getmyblogs")
    public void getMyblgs(HttpServletResponse response,HttpServletRequest request){
        String username = JwtUtil.getTokenInfo(request.getHeader("authorization")).getClaim("username").asString();
        log.info(username);
        List<BDetails> list = blogDetailService.selectByEditor(username);

        JSONTransferUtil.setResp(response);
        try {
            response.getWriter().write(JSONTransferUtil.transferBeanToJsonArray(list));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @RequestMapping("/uploadBlog")
    @Transactional
    public void upload(HttpServletRequest request, HttpServletResponse response,
                @RequestParam(name = "content") String content,
                @RequestParam(name = "title") String title,
                @RequestParam(name = "tags[tagId]") String[] tagIds,
                @RequestParam(name = "tags[tagName]") String[] tagNames,
                @RequestParam(name = "description") String description,
                @RequestParam(name = "blogId") String blogId,
                @RequestParam(name = "imgUrl")String imgUrl){
        log.info("-----Uploading Blog-----");
        String authorization = request.getHeader("authorization");
        String username = JwtUtil.getTokenInfo(authorization).getClaim("username").asString();

        System.out.println(content);
        Blog blog = new Blog();
        blog.setContent(content);
        blog.setTitle(title);
        blog.setEditor(username);
        blog.setEditTime(getCurTime());
        blog.setBlogId(blogId);

        BDetails bDetails = blogDetailService.selectByPrimaryKey(blogId);
        if (bDetails == null)
        {
            bDetails = new BDetails();
            bDetails.setEditor(username);
            bDetails.setEditTime(blog.getEditTime());
            bDetails.setBlogId(blogId);
            bDetails.setClickRate(0);
            bDetails.setCollectNum(0);
            bDetails.setTitle(title);
            bDetails.setImgUrl(imgUrl);
            bDetails.setTag1(null);
            bDetails.setTag2(null);
            bDetails.setTag3(null);
            bDetails.setTag4(null);
            bDetails.setTag5(null);
            bDetails.setDescription(description);
        }

        //更新tag
        //有一定问题，有必要修改一下
        //此时tag已经在数据库中存在怎么办
        int len = tagIds.length;
        if (tagIds.length != tagNames.length)
        {
            if (tagIds.length>tagNames.length)
                len = tagNames.length;
            else if (tagIds.length< tagIds.length)
                len = tagIds.length;
        }

        ValueOperations<String ,List> opsForValue = redisTemplate.opsForValue();
        List<BTag> tags = opsForValue.get(Constant.BLOG_TAGS);
        Map<String,String> tn = new HashMap<>();
        int size = tags.size();
        for (BTag tag : tags) {
            tn.put(tag.getTagName(),tag.getTagId());
        }
        //tag更新
        for (int i=0 ;i<len; i++)
        {
            //如果原数据库已经有这个tag
            if (tn.containsKey(tagNames[i]))
            {
                if (bDetails.getTag1()==null)
                    bDetails.setTag1(tn.get(tagNames[i]));
                else if (bDetails.getTag2() == null)
                    bDetails.setTag2(tn.get(tagNames[i]));
                else if (bDetails.getTag3() == null)
                    bDetails.setTag3(tn.get(tagNames[i]));
                else if (bDetails.getTag4() == null)
                    bDetails.setTag4(tn.get(tagNames[i]));
                else if (bDetails.getTag5() == null)
                    bDetails.setTag5(tn.get(tagNames[i]));
            }
            else {
                //如果原数据库没有这个tag
                BTag bTag = new BTag();
                bTag.setTagId(tagIds[i]);
                bTag.setTagName(tagNames[i]);
                int insert = bTagService.insert(bTag);
                if (insert != 0)
                {
                    if (bDetails.getTag1()==null)
                        bDetails.setTag1(bTag.getTagId());
                    else if (bDetails.getTag2() == null)
                        bDetails.setTag2(bTag.getTagId());
                    else if (bDetails.getTag3() == null)
                        bDetails.setTag3(bTag.getTagId());
                    else if (bDetails.getTag4() == null)
                        bDetails.setTag4(bTag.getTagId());
                    else if (bDetails.getTag5() == null)
                        bDetails.setTag5(bTag.getTagId());

                    tags.add(bTag);
                }
                else {
                    log.info("插入tag:"+tagNames[i]+" 出错");
                }
            }
        }

        //更新redis
        if (size != tags.size())
        {
            opsForValue.set(Constant.BLOG_TAGS,tags);
        }
        int detailFlag = blogDetailService.updateByPrimaryKey(bDetails);
        int blogFlag = blogService.updateByPrimaryKeyWithBLOBs(blog);

        log.info("detailFlag"+detailFlag+" "+"blogFlag"+blogFlag);
        if (detailFlag==0 || blogFlag == 0)
        {
            detailFlag = blogDetailService.insert(bDetails);

            blogFlag = blogService.insert(blog);
        }


        FeedBack feedBack = new FeedBack();
        JSONTransferUtil.setResp(response);
        if (blogFlag ==0 || detailFlag == 0)
        {
            try {
                feedBack.setStatus(0);
                feedBack.setMessage("上传失败!");
                response.getWriter().write(JSONTransferUtil.transferBeanToJson(feedBack));
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            feedBack.setStatus(1);
            feedBack.setMessage("上传成功!");
            Node node = new Node();
            node.setKey(bDetails.getClickRate());
            node.setValue(bDetails.getBlogId());
            node.setbDetail(bDetails);
            System.out.println("插入新结点");
            homePageController.updateRankTree(node);

            //移除博客信息
            ValueOperations<String, Map> opt = redisTemplate.opsForValue();
            Map<String,String> map = opt.get(Constant.NEW_BLOG_ID);
            map.remove(username+"/blog/"+blogId);
            opt.set(Constant.NEW_BLOG_ID,map);
            try {
                response.getWriter().write(JSONTransferUtil.transferBeanToJson(feedBack));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @RequestMapping("delBlog")
    @Transactional
    public void deleteBlog(HttpServletResponse response,
                           HttpServletRequest request,
                           @RequestParam(name = "blog_id") String blog_id)
    {
        FeedBack feedBack = new FeedBack();
        JSONTransferUtil.setResp(response);
        int flag = blogDetailService.deleteByPrimaryKey(blog_id);
        if (flag == 0)
            try {
                feedBack.setStatus(0);
                feedBack.setMessage("删除博客详情出错，待删除博客详情:"+blog_id);
                response.getWriter().write(JSONTransferUtil.transferBeanToJson(feedBack));
                throw new Exception("删除博客详情出错，待删除博客详情:"+blog_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        flag = blogService.deleteByPrimaryKey(blog_id);
        if (flag == 0)
            try {
                feedBack.setStatus(0);
                feedBack.setMessage("删除博客出错，待删除博客:"+blog_id);
                response.getWriter().write(JSONTransferUtil.transferBeanToJson(feedBack));
                throw new Exception("删除博客出错，待删除博客:"+blog_id);
            } catch (Exception e) {
                e.printStackTrace();
            }


        Node node = homePageController.searchNode(blog_id);
        if (node != null)
        {
            homePageController.deleteNode(node);
        }

        String username = JwtUtil.getTokenInfo(request.getHeader("authorization")).getClaim("username").asString();
        String filepath = "file/"+username+"/blog/"+blog_id;


        ossClient.deleteObject("easypalace",filepath);
        try {
            feedBack.setStatus(1);
            feedBack.setMessage("删除成功!");
            response.getWriter().write(JSONTransferUtil.transferBeanToJson(feedBack));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private String getCurTime()
    {
        return simpleDateFormat.format(new Date());
    }

 }
