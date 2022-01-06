package com.ctgu.hardworkingserver.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class Msg {
    private Integer id;

    private String userto;

    private String userfrom;

    private String title;

    private String mtime;

    private String rely;

    private Integer status;

    private String content;


}