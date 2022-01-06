package com.ctgu.hardworkingserver.entity;


import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@ToString
public class PubFile {
    private Integer id;

    private String username;

    private String filename;

    private String loc;

    private Integer collectnum;

    private Integer downloadnum;

    private Boolean isplayable;

    private Boolean ismusic;

    private Boolean isvideo;

    private Boolean ispic;

    private String msize;

    private String mtime;

    private Boolean iscollected;

    private Integer cid;

    private Boolean type = false;

}