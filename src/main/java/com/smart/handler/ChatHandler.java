package com.smart.handler;

import com.smart.common.properties.TalkProperties;
import com.smart.constants.SentConstant;
import com.smart.mapper.UserMapper;
import com.smart.pojo.entity.Talk;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.CloseStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ChatHandler extends TextWebSocketHandler {
    @Autowired
    UserMapper userMapper;

    @Autowired
    TalkProperties talkProperties;

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate redisTemplate;
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session)  {
        Map<String, Object> attributes = session.getAttributes();
        Integer userId = (Integer) attributes.get("userId");
        SentConstant.concurrentHashMap.put(userId, session);

       log.info("连接成功{}",session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
       log.info("收到消息{}",session.getAttributes().get("userId"));
        String payload = message.getPayload();
        Integer userId = Integer.parseInt((String) session.getAttributes().get("userId"));
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        //此处追加了历史记录
        redisTemplate.opsForValue().append(talkProperties.getCacheName()+"::"+userId, "用户："+payload+"\n");

        params.add(new BasicNameValuePair("userId", userId.toString()));
        params.add(new BasicNameValuePair("message", payload));

        //暂时先不转发给ai
        //doGetWithSSE(talkProperties.getUrl(),params);


    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        String content = (String) redisTemplate.opsForValue().getAndDelete(talkProperties.getCacheName() + "::" + session.getAttributes().get("userId"));
        Talk talk = Talk.builder().userId((Integer) session.getAttributes().get("userId")).id((Integer) session.getAttributes().get("userId")).content(content).build();
        userMapper.updateTalk(talk);
        SentConstant.concurrentHashMap.remove(session.getAttributes().get("userId"));

    }
    public void doGetWithSSE(String url, List<NameValuePair> params) throws URISyntaxException, IOException {

        //创建 URI 对象，并添加查询参数
        URIBuilder uriBuilder = new URIBuilder(url);
        uriBuilder = uriBuilder.addParameters(params);

        // 创建 HttpGet 请求
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.addHeader("Accept", "text/event-stream"); // 设置接受 SSE 事件流

        // 创建 HttpClient
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // 执行请求
        CloseableHttpResponse response = httpclient.execute(httpGet);

        try {
            // 获取响应的实体流
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // 使用 BufferedReader 逐行读取流
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                StringBuilder eventBuilder = new StringBuilder(); // 用于暂存当前事件
                StringBuilder history = new StringBuilder();
                history.append("AI:");
                WebSocketSession session = null;
                Integer userId = null;
                while ((line = reader.readLine()) != null) {
                    // 检测空行，表示当前事件结束
                    if (line.isEmpty()) {
                        // 处理完整事件
                        Map<String, String> eventMap = processEvent(eventBuilder.toString());
                        //获取session
                        userId =Integer.parseInt(eventMap.get("userId"));
                        session = SentConstant.concurrentHashMap.get(userId);

                        //把回复转发给用户
                        history.append(eventMap.get("data"));
                        session.sendMessage(new TextMessage(eventMap.get("data")));
                        eventBuilder.setLength(0); // 清空当前事件缓冲区
                    } else {
                        // 累积事件数据
                        eventBuilder.append(line).append("\n");
                    }
                }

                if(session!=null){
                    //存入历史记录
                    session.sendMessage(new TextMessage("end"));
                    redisTemplate.opsForValue().append(talkProperties.getCacheName()+"::"+userId,history+"\n");
                }

            }
        } finally {
            // 关闭 HttpClient 连接
            httpclient.close();
        }
    }

    // 解析和处理每个事件的方法
    private  Map<String,String> processEvent(String rawEvent) {
        // 按行分割事件
        String[] lines = rawEvent.split("\n");
        String eventType = "message"; // 默认事件类型
        String eventData = "";
        String eventId = null;

        for (String line : lines) {
            if (line.startsWith("event: ")) {
                eventType = line.substring(7).trim();
            } else if (line.startsWith("userId: ")) {
                eventId = line.substring(4).trim();
            } else if (line.startsWith("data: ")) {
                eventData += line.substring(6).trim() + "\n"; // 支持多行数据
            }
        }

        // 去掉末尾多余的换行符
        eventData = eventData.trim();
        Map<String,String> map = new HashMap<String,String>();
        map.put("eventType", eventType);
        map.put("eventData", eventData);
        map.put("eventId", eventId);
        return map;

    }
}
