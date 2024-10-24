package com.smart.mapper;

import com.smart.annotation.AutoFill;
import com.smart.constants.OperationConstant;
import com.smart.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select *from smart.user where user_name = #{userName}")
    User findByUserName(String userName);
    @AutoFill(operationType = OperationConstant.INSERT)
    @Insert("insert into smart.user (user_name, password,create_time,update_time) values (#{userName},#{password},#{createTime},#{updateTime}) ")
    void createUser(User user);
}
