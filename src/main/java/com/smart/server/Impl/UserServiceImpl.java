package com.smart.server.Impl;

import com.smart.Util.HttpUtil;
import com.smart.Util.JwtUtil;
import com.smart.common.properties.JwtProperties;
import com.smart.common.properties.TalkProperties;
import com.smart.common.result.Result;
import com.smart.constants.MessageConstant;
import com.smart.constants.RegexConstant;
import com.smart.mapper.UserMapper;
import com.smart.pojo.dto.TalkDto;
import com.smart.pojo.dto.UserDto;
import com.smart.pojo.entity.User;
import com.smart.pojo.vo.TalkVo;
import com.smart.server.UserService;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    TalkProperties talkProperties;

    @Override
    public Result register(UserDto userDto) {
        User user =  userMapper.findByUserName(userDto.getUsername());
        //查看用户名是否存在
        if(user != null) {
            return Result.error(MessageConstant.ACCOUNT_ALREADY_EXISTS);
        }
        String username = userDto.getUsername();
        String password = userDto.getPassword();
        //查看用户名是否长度超限   3<name<16
        if (username.length() < 3||username.length() > 16||!username.matches(RegexConstant.USERNAME)) {
            return Result.error(MessageConstant.USERNAME_OR_PASSWORD_OUT_OF_RANGE);
        }

        if (password.length() < 6||password.length() > 16||!password.matches(RegexConstant.PASSWORD)) {
            return Result.error(MessageConstant.USERNAME_OR_PASSWORD_OUT_OF_RANGE);
        }

        user = new User();

        //md5摘要，生成32位密文
        String md5Pwd = DigestUtils.md5DigestAsHex((password).getBytes());
        user.setUsername(username);
        user.setPassword(md5Pwd);
        userMapper.createUser(user);
        return Result.success();

    }


    @Override
    public Result login(UserDto userDto) {
        userDto.setPassword(DigestUtils.md5DigestAsHex(userDto.getPassword().getBytes()));
        User user = userMapper.login(userDto);
        if (user == null) {
            return Result.error(MessageConstant.USERNAME_OR_PASSWORD_INCORRECT);
        }
        else {
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            String jwt = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
            return Result.success(jwt);
        }
    }




    @Override
    public String talk(String text) throws URISyntaxException, IOException, ParseException {
        BasicNameValuePair param = new BasicNameValuePair("text", text);
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(param);
        return HttpUtil.doGet(talkProperties.getUrl(), params);
    }

    @Override
    public TalkVo selectTalk(TalkDto talkDto) {
        return userMapper.findTalkById(talkDto);
    }

    @Override
    public List<TalkVo> getTalks(int userId) {
        return userMapper.getTalks(userId);
    }

}
