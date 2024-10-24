package com.smart.constants;

public class RegexConstant {
    //密码，英文和数字一起组成
    public static final String PASSWORD = "^(?=.*[a-zA-Z])(?=.*\\d).+$";
    //用户名4-16位，允许字母数字下划线和减号
    public static final String USERNAME = "^[\\w-]{3,16}$";

}
