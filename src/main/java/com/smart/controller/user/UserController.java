package com.smart.controller.user;


import com.smart.common.result.Result;
import com.smart.constants.MessageConstant;
import com.smart.pojo.dto.UserDto;
import com.smart.server.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    public Result login(@RequestBody UserDto userDto) {



        return Result.success();
    }

}
