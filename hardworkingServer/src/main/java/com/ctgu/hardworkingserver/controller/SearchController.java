package com.ctgu.hardworkingserver.controller;


import com.ctgu.hardworkingserver.entity.BDetails;
import com.ctgu.hardworkingserver.entity.BTag;
import com.ctgu.hardworkingserver.entity.PubFile;
import com.ctgu.hardworkingserver.service.BTagServiceImp;
import com.ctgu.hardworkingserver.service.BlogDetailServiceImp;
import com.ctgu.hardworkingserver.service.PrivateSpaceServiceImp;
import com.ctgu.hardworkingserver.service.PublicSpaceServiceImp;
import com.ctgu.hardworkingserver.utils.JSONTransferUtil;
import com.ctgu.hardworkingserver.utils.JwtUtil;
import com.ctgu.hardworkingserver.utils.Node;
import com.ctgu.hardworkingserver.utils.RankBinaryTree;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private PublicSpaceServiceImp publicSpaceService;

    @Autowired
    private PrivateSpaceServiceImp privateSpaceService;

    @Autowired
    BTagServiceImp tagService;

    @Autowired
    public Node root;

    @Autowired
    public BlogDetailServiceImp blogDetailService;


    @RequestMapping("/files")
    public void searchFiles(HttpServletRequest request,
                            HttpServletResponse response,
                            @RequestParam(name = "key")String key,
                            @RequestParam(name = "smode")boolean mode)
    {
        key = "%"+key+"%";
        List<PubFile> list = publicSpaceService.vagueQuery(key);
//        log.info(list.toString());
        JSONTransferUtil.setResp(response);
        if (mode)
        {
            String username = JwtUtil.getTokenInfo(request.getHeader("authorization")).getClaim("username").asString();

            //首先查询publiclist
            List<PubFile> priList = privateSpaceService.vagueQuery(username,key);
//            log.info(priList.toString());
            for (PubFile pubFile : priList) {
                pubFile.setType(true);
            }
            list.addAll(priList);
        }
        try {
//            log.info(JSONTransferUtil.transferBeanToJsonArray(list));
            response.getWriter().write(JSONTransferUtil.transferBeanToJsonArray(list));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/blogbytag")
    public void query(HttpServletResponse response,
                      @RequestParam(name = "tagId")String tagId){
        BTag bTag = tagService.selectByPrimaryKey(tagId);
        log.info(bTag.toString());
        List<BDetails> list = RankBinaryTree.InOrderTraverseSout(root, tagId);
        JSONTransferUtil.setResp(response);
        try {
            response.getWriter().write(JSONTransferUtil.transferBeanToJsonArray(list));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/blogbyname")
    public void find(HttpServletResponse response,
                      @RequestParam(name = "key")String key){

        key = "%"+key+"%";
        List<BDetails> list = blogDetailService.vagueSearch(key);
        JSONTransferUtil.setResp(response);
        try {
            response.getWriter().write(JSONTransferUtil.transferBeanToJsonArray(list));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRoot(Node root) {
        this.root = root;
    }
}
