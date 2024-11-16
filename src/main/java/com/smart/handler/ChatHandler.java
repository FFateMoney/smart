package com.smart.handler;

import com.smart.Util.HttpUtil;
import com.smart.common.properties.TalkProperties;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class ChatHandler extends TextWebSocketHandler {
    @Autowired
    TalkProperties talkProperties;


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
        System.out.println("收到消息"+message.getPayload());
        System.out.println("当前线程为："+Thread.currentThread().getName());


        String payload = message.getPayload();
        Integer userId = Integer.parseInt((String) session.getAttributes().get("userId"));
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userId", userId.toString()));
        params.add(new BasicNameValuePair("message", payload));
        HttpUtil.doGetWithSSE(talkProperties.getUrl(),params);

        //TODO添加进历史记录




    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //移除redis里面的键值对
    }


}
