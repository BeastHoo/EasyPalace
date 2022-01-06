package com.ctgu.hardworkingserver.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/*
*
* blog bean
* author: chengwei
*
* */
@Data
@ToString
public class Blog implements Serializable {
    private String blogId;

    private String editor;


    private String title;

    private String editTime;

    private String content;

}