package com.smart.controller.user;
import com.smart.common.result.Result;
import com.smart.constants.MessageConstant;
import com.smart.pojo.dto.TalkDto;
import com.smart.pojo.dto.UserDto;
import com.smart.pojo.vo.TalkVo;
import com.smart.server.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
@RestController
@Api("用户控制接口")
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/register")
    @ApiOperation("注册接口")
    public Result register(@RequestBody UserDto userDto) {
        Result result =  userService.register(userDto);
        //返回注册结果
        return result;
    }

    @GetMapping("/login")
    @ApiOperation("登入接口")
    public Result login( UserDto userDto) {
        log.info("开始登入:{}",userDto.getUsername());
        return userService.login(userDto);
    }

    @GetMapping("/getTalks")
    @ApiOperation("获取所有聊天的标题")
    public Result getAllTalks(@RequestAttribute("userId") int userId) {
        return Result.success(userService.getTalks(userId));
    }

    //待完善，需要配置redis
    @GetMapping("/selectTalk")
    @ApiOperation("选择聊天，实际上是获取聊天记录")
    public Result selectTalk(@RequestAttribute("userId")Integer userId,@NonNull Integer talkId) {
        TalkDto talkDto = new TalkDto(talkId,userId);
        TalkVo talkVo = userService.selectTalk(talkDto);
        if (talkVo != null&&talkVo.getId()!=-1) {
            return Result.success(talkVo);
        }
        else return Result.error(MessageConstant.TALK_NOT_EXIT);
    }



    //还需要对聊天的数量进行限制
    @PostMapping("/createTalk")
    @ApiOperation("新建聊天")
    public Result createTalk(@RequestAttribute("userId")Integer userId){
        TalkVo talkVo =  userService.createTalk(userId);
        return Result.success(talkVo);
    }

    //TODO AI部署好再完善
    @PutMapping("/talk")
    @ApiOperation("对话接口")
    public Result talk(@RequestBody String text) throws URISyntaxException, IOException, ParseException {
        String response = userService.talk(text);

        return Result.success(response);
    }
}
