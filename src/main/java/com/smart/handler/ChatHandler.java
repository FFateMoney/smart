package com.smart.handler;

import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class ChatHandler extends TextWebSocketHandler {

    // 使用一个 Map 来存储 token 与 WebSocketSession 的映射
    private static final ConcurrentHashMap<Integer, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //在这里做一个键值对存到redis里，键是用户id，存在 attributes里面，值是session.以方便后续取session
        /*
        Map<String, Object> attributes = session.getAttributes();
        Integer userId = (Integer) attributes.get("userId");
        userSessions.put(userId, session);
         */
        System.out.println("连接成功"+session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        //Integer userId = Integer.parseInt((String) session.getAttributes().get("userId"));
        System.out.println("收到消息"+message.getPayload());
        System.out.println("当前线程为："+Thread.currentThread().getId());
        Thread.sleep(4000);
        if (false) {
            return;
        }
        //payload = "用户："+payload;
        //添加进历史记录

        //给ai发送信息
        //session.sendMessage(new TextMessage(userId +"\n"+payload));



        //TODO 此处需要解决问题，如果ai是一个一个字返回的，且有多个用户，应该如何识别每个字的归属用户？
        //String[] split = payload.split("\n", 1);
        //payload = "AI："+payload;
        
        /*
        1.首先获取session的用户id，然后判断这个用户是ai还是普通用户
        如果是ai：
        1.如果是ai则检查message，查看目标用户是谁
        2.可以直接转发给用户，然后加入到历史记录中；也可以第二个方法，转发到一个消息管理器中统一管理

        如果是用户：
        1.收到消息后首先检查redis中上一次的状态，如果上一次对话没有被ai处理完，则直接返回，这里前端会做一个页面上的阻止，这是用来防止前端的绕过
        2.先存到redis历史记录缓存中，然后有两种办法，一个是直接转发给ai，第二个是放到消息管理器中统一处理
        */


    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //移除redis里面的键值对
    }


}
