package com.smart.server;

import com.smart.common.result.Result;
import com.smart.pojo.dto.UserDto;

public interface UserService {
    Result register(UserDto userDto);


}
