package com.ctgu.hardworkingserver.controller;


import com.ctgu.hardworkingserver.entity.BDetails;
import com.ctgu.hardworkingserver.entity.Blog;
import com.ctgu.hardworkingserver.service.BlogDetailServiceImp;
import com.ctgu.hardworkingserver.service.BlogServiceImp;
import com.ctgu.hardworkingserver.utils.JSONTransferUtil;
import com.ctgu.hardworkingserver.utils.Node;
import com.ctgu.hardworkingserver.utils.RankBinaryTree;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/blog")
public class HomePageController {

    @Autowired
    public Node root;

    @Autowired
    public BlogServiceImp blogService;

    @Autowired
    public BlogDetailServiceImp blogDetailService;

    @Autowired
    public SearchController searchController;


    @RequestMapping("/hotBlogs")
    public void getRank(HttpServletResponse response){
        List<BDetails> bDetailsList = RankBinaryTree.InOrderTraverse(root);
        log.info("--------获取点击排行榜----------");
        JSONTransferUtil.setResp(response);
        String s = JSONTransferUtil.transferBeanToJsonArray(bDetailsList);

        try {
            response.getWriter().write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/getblog")
    public void getBlog(HttpServletResponse response
            , @RequestParam(name = "bid") String bid){
        Blog blog = null;
        if (bid!=null)
        {
            blog = blogService.selectByPrimaryKey(bid);
        }
        blogDetailService.updateClickRateByPrimaryKey(bid);
        BDetails bDetails = blogDetailService.selectByPrimaryKey(bid);


        //更新rankTree
        Node mNode = RankBinaryTree.TreeSearch(root, bDetails.getBlogId());

        if (mNode!=null)
        {
//            删除结点
            RankBinaryTree.Delete(root,mNode);
            mNode.setKey(bDetails.getClickRate());

            //设置结点为空
            mNode.setLeft(null);
            mNode.setRight(null);
            mNode.getbDetail().setClickRate(mNode.getKey());

            //插入结点
            RankBinaryTree.Node_Insert(root,mNode);
        }
        else
        {
            Node node = new Node();
            node.setKey(bDetails.getClickRate());
            node.setValue(bDetails.getBlogId());
            node.setbDetail(bDetails);
        RankBinaryTree.Node_Insert(root,node);
        }

        JSONTransferUtil.setResp(response);
        try {
            response.getWriter().write(JSONTransferUtil.transferBeanToJson(blog));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/getImg/{username}")
    private void getImage(@PathVariable String username, HttpServletRequest request, HttpServletResponse response){
        log.info("获取图片资源");
        log.info(username);
        String uri = username;
        String[] split = uri.split("&");
        String[] uParam = split[0].split("=");
        String uname = uParam[1];

        String[] tParam = split[1].split("=");
        String type = tParam[1];

        String filepath = null;
//        "./FILE/"+username+"/"+blogId+"/"+filename
        if (type.equals("blog"))
        {
            String[] bParam = split[2].split("=");
            String blogId = bParam[1];
            String[] fParam = split[3].split("=");
            String filename = fParam[1];
            filepath = "./FILE/"+uname+"/BLOG/"+blogId+"/"+filename;
        }else {
            String[] fParam = split[2].split("=");
            String filename = fParam[1];
            filepath = "./FILE/"+uname+"/"+filename;
        }



        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(new File(filepath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bis = new BufferedInputStream(fis);

        int i = 0;
        try {
            OutputStream outputStream = response.getOutputStream();
            i = bis.read(buffer);
            while (i != -1) {
                outputStream.write(buffer,0,i);
                i = bis.read(buffer);
            }
            bis.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequestMapping("/findblogbyuser")
    public void findBlogByUsername(HttpServletResponse response,@RequestParam(name = "username")String username){
        List<BDetails> list = blogDetailService.selectByEditor(username);
        JSONTransferUtil.setResp(response);
        try {
            response.getWriter().write(JSONTransferUtil.transferBeanToJsonArray(list));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @RequestMapping("/getDetails")
    public void getDetails(HttpServletResponse response,
                           @RequestParam(name = "bid")String bid){
        Node node = searchNode(bid);
        BDetails bDetails = node.getbDetail();
        JSONTransferUtil.setResp(response);
        String s = JSONTransferUtil.transferBeanToJson(bDetails);
        try {
            response.getWriter().write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void updateRankTree(Node k){
        Node node = RankBinaryTree.TreeSearch(root, k.getValue());
        if (node == root || node == null)
        {
            RankBinaryTree.Node_Insert(root,k);
        }
        else {
            RankBinaryTree.Delete(root,node);
            RankBinaryTree.Node_Insert(root,k);
        }

//        RankBinaryTree.InOrderTraverseSout(root);
        searchController.setRoot(root);

    }

    public void deleteNode(Node k)
    {
        RankBinaryTree.Delete(root,k);
    }

    public Node searchNode(String blog_id) {
        return RankBinaryTree.TreeSearch(root,blog_id);
    }
}
