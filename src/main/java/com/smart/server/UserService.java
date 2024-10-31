package com.smart.server;

import com.smart.common.result.Result;
import com.smart.pojo.dto.TalkDto;
import com.smart.pojo.dto.UserDto;
import com.smart.pojo.entity.Talk;
import com.smart.pojo.vo.TalkVo;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface UserService {
    Result register(UserDto userDto);
    Result login(UserDto userDto);

    String talk(String text) throws URISyntaxException, IOException, ParseException;

    TalkVo selectTalk(TalkDto talkDto);
}
