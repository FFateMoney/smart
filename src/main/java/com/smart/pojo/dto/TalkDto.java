package com.smart.pojo.dto;

import lombok.*;

import javax.validation.Valid;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TalkDto {
    private Integer id;

    private Integer userId;
}
