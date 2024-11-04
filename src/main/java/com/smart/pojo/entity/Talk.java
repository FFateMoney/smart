package com.smart.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Talk {
    private int id;
    private int userId;
    private String title;
    private String content;
    private String createTime;
    private String updateTime;
}
