package com.smart.server.Impl;

import com.smart.mapper.UserMapper;
import com.smart.pojo.dto.UserDto;
import com.smart.pojo.entity.User;
import com.smart.server.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Override
    public Boolean register(UserDto userDto) {
        User user =  userMapper.findByUserName(userDto.getUserName());
        if(user != null) {
            return false;
        }
        else {
            user = new User();
            user.setUserName(userDto.getUserName());
            user.setPassword(userDto.getPassword());
            userMapper.createUser(user);
            return true;
        }
    }

    //TODO 待完善
}
