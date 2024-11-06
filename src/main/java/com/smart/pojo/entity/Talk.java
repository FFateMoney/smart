package com.smart.pojo.entity;

import lombok.*;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Component
public class Talk implements Serializable {
    private int id;
    private int userId;
    private String title;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;


}
