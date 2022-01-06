package com.ctgu.hardworkingserver.utils;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FeedBack {
    //状态
    private int status;
    //信息
    private String message;

}
