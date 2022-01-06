package com.ctgu.hardworkingserver.controller;

import com.ctgu.hardworkingserver.entity.BDetails;
import com.ctgu.hardworkingserver.entity.BTag;
import com.ctgu.hardworkingserver.service.BTagServiceImp;
import com.ctgu.hardworkingserver.utils.JSONTransferUtil;
import com.ctgu.hardworkingserver.utils.Node;
import com.ctgu.hardworkingserver.utils.RankBinaryTree;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


@RestController
@Slf4j
@RequestMapping("/tag")
public class TagController {
    @Autowired
    BTagServiceImp tagService;

    @RequestMapping("/getTags")
    public void getTags(HttpServletResponse response){
        List<BTag> bTags = tagService.selectAll();
        List<BTag> workList=null;
        if (bTags.size()<=15)
        {
            workList=bTags;
        }else {
            workList = new ArrayList<>();
            Random random = new Random();

            for (int i=0;i<15;i++)
            {
                int index = random.nextInt(bTags.size());
                workList.add(bTags.get(index));
            }

            Collections.shuffle(workList);
        }
        JSONTransferUtil.setResp(response);
        try {
            response.getWriter().write(JSONTransferUtil.transferBeanToJsonArray(workList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
