package com.smart.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {
    private int id;
    private String username;
    private String password;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private ArrayList<Favor> favors = new ArrayList<Favor>();

}
