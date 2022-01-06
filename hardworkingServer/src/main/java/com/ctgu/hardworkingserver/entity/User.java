package com.ctgu.hardworkingserver.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;


@Data
@ToString
public class User implements Serializable {
    private String username;

    private String password;

    private String email;
    //注册时间
    private String registtime;
    //加密盐
    private String salt;

    private String signature;

    private String imageUrl;

    private String birthday;

    private String slogan;

    private String gender;

}