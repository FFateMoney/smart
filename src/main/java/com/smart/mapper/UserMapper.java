package com.smart.mapper;

import com.smart.annotation.AutoFill;
import com.smart.constants.OperationConstant;
import com.smart.pojo.dto.TalkDto;
import com.smart.pojo.dto.UserDto;
import com.smart.pojo.entity.Talk;
import com.smart.pojo.entity.User;
import com.smart.pojo.vo.TalkVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

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
    @Cacheable(cacheNames = "talks",key = "#talkDto.userId")
    @Select("select talk.id,talk.content,create_time,update_time from smart.talk where user_id = #{userId} and id = #{id}")
    Talk findTalkById(TalkDto talkDto);

    //获取一个用户所有的对话id和标题
    @Select("select talk.id,talk.title from smart.talk where user_id = #{userId}")
    List<TalkVo> getTalks(int userId);

    //创建新对话
    @AutoFill(operationType = OperationConstant.INSERT)
    @Insert("insert into talk (user_id,title,create_time,update_time) values (#{userId},#{title},#{createTime},#{updateTime}) ")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void createTalk(Talk talk);
}
