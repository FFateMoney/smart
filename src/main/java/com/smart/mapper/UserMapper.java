package com.smart.mapper;

import com.smart.annotation.AutoFill;
import com.smart.constants.OperationConstant;
import com.smart.pojo.dto.TalkDto;
import com.smart.pojo.dto.UserDto;
import com.smart.pojo.entity.User;
import com.smart.pojo.vo.TalkVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    //按用户名查询用户
    @Select("select *from smart.user where user.username = #{username}")
    User findByUserName(String username);

    //创建新用户
    @AutoFill(operationType = OperationConstant.INSERT)
    @Insert("insert into smart.user (username, password,create_time,update_time) values (#{username},#{password},#{createTime},#{updateTime}) ")
    void createUser(User user);

    //用户登入

    @Select("select *from smart.user where username = #{username} and password = #{password}")
    User login(UserDto userDto);

    //根据用户id和对话id查询聊天记录
    @Select("select (talk_history.context) from smart.talk_history where user_id = #{userId} and id = #{id}")
    TalkVo findTalkById(TalkDto talkDto);
}
