package com.smart.pojo.entity;

import lombok.Data;

import java.util.ArrayList;

@Data
public class User {
    private int id;
    private String userName;
    private String password;
    private String createTime;
    private String updateTime;
    private ArrayList<Favor> favors = new ArrayList<Favor>();

}
