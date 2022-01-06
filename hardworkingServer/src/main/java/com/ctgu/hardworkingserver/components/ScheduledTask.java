package com.ctgu.hardworkingserver.components;


import com.aliyun.oss.OSS;
import com.ctgu.hardworkingserver.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class ScheduledTask {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    OSS ossClient;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:MM:ss");

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void refresh(){
        log.info("----****开始定时任务****----");
        ValueOperations<String, Map> opt = redisTemplate.opsForValue();
        Map<String,String> map = opt.get(Constant.NEW_BLOG_ID);
        Date current = new Date();
        assert map != null;
        for (Map.Entry<String,String> entry : map.entrySet())
        {
            try {
                Date oldDate = simpleDateFormat.parse(entry.getValue());
                long mills = current.getTime()- oldDate.getTime();
                long minutes = mills/(1000*60);
                if (minutes>=60)
                {
                    String path = "file/"+entry.getKey();
                    ossClient.deleteObject("easypalace",path);
                    map.remove(entry.getKey());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        opt.set(Constant.NEW_BLOG_ID,map);
        log.info("----****定时任务结束****----");
    }

}
