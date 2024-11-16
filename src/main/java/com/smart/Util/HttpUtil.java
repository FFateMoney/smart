package com.smart.Util;

import com.smart.constants.SentConstant;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HttpUtil {
    public static BlockingQueue blockingQueue=  new LinkedBlockingQueue();


    public static String doGet(String url, List<NameValuePair> params) throws URISyntaxException, IOException, ParseException {
        //创建一个URI对象，然后创建get请求
        URIBuilder uriBuilder = new URIBuilder(url);
        uriBuilder = uriBuilder.addParameters(params);
        HttpGet httpGet = new HttpGet(uriBuilder.build());

        //创建请求对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //请求对象发送请求然后获得回应
        CloseableHttpResponse response = httpclient.execute(httpGet);

        //获取相应实体
        HttpEntity entity = response.getEntity();

        String res = EntityUtils.toString(entity);

        httpclient.close();
        //return entity.toString();
        //return response.toString();
        return res;
    }

    public static void doGetWithSSE(String url, List<NameValuePair> params) throws URISyntaxException, IOException {

        //TODO 把ai的消息存进历史记录
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

                while ((line = reader.readLine()) != null) {
                    // 检测空行，表示当前事件结束
                    if (line.isEmpty()) {
                        // 处理完整事件
                        Map<String, String> eventMap = processEvent(eventBuilder.toString());
                        WebSocketSession session = SentConstant.concurrentHashMap.get(eventMap.get("userId"));
                        session.sendMessage(new TextMessage(eventMap.get("data")));
                        eventBuilder.setLength(0); // 清空当前事件缓冲区
                    } else {
                        // 累积事件数据
                        eventBuilder.append(line).append("\n");
                    }
                }
            }
        } finally {
            // 关闭 HttpClient 连接
            httpclient.close();
        }
    }

    // 解析和处理每个事件的方法
    private static Map<String,String> processEvent(String rawEvent) {
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