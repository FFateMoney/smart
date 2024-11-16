package com.smart.constants;

import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

 public class SentConstant  {
    // 使用一个 Map 来存储 token 与 WebSocketSession 的映射
    public static ConcurrentHashMap<Integer,WebSocketSession> concurrentHashMap=  new ConcurrentHashMap<>();




}
