package com.ctgu.hardworkingserver.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data

public class BDetails {
    private String blogId;

    private Integer clickRate;

    private Integer collectNum;

    private String tag1;

    private String tag2;

    private String tag3;

    private String tag4;

    private String tag5;

    private String title;

    private String imgUrl;

    private String editor;

    private String editTime;

    private String description;

    @Override
    public String toString() {
        return "BDetails{" +
                "blogId=" + blogId +
                ", clickRate=" + clickRate +
                ", collectNum=" + collectNum +
                ", tag1=" + tag1 +
                ", tag2=" + tag2 +
                ", tag3=" + tag3 +
                ", tag4=" + tag4 +
                ", tag5=" + tag5 +
                ", title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", editor='" + editor + '\'' +
                ", editTime='" + editTime + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}