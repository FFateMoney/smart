package com.smart.server.Impl;

import com.smart.common.result.Result;
import com.smart.constants.MessageConstant;
import com.smart.constants.RegexConstant;
import com.smart.mapper.UserMapper;
import com.smart.pojo.dto.UserDto;
import com.smart.pojo.entity.User;
import com.smart.server.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Override
    public Result register(UserDto userDto) {
        User user =  userMapper.findByUserName(userDto.getUserName());
        //查看用户名是否存在
        if(user != null) {
            return Result.error(MessageConstant.ACCOUNT_ALREADY_EXISTS);
        }
        String userName = userDto.getUserName();
        String password = userDto.getPassword();
        //查看用户名是否长度超限   3<name<16
        if (userName.length() < 3||userName.length() > 16||!userName.matches(RegexConstant.USERNAME)) {
            return Result.error(MessageConstant.USERNAME_OR_PASSWORD_OUT_OF_RANGE);
        }

        if (password.length() < 6||password.length() > 16||!password.matches(RegexConstant.PASSWORD)) {
            return Result.error(MessageConstant.USERNAME_OR_PASSWORD_OUT_OF_RANGE);
        }

        user = new User();

        //md5摘要，生成32位密文
        String md5Pwd = DigestUtils.md5DigestAsHex((password).getBytes());
        user.setUserName(userName);
        user.setPassword(md5Pwd);
        userMapper.createUser(user);
        return Result.success();

    }

}
