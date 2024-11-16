package com.smart.Util;

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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class HttpUtil {
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

    public static void doGetWithSSE(String url, List<NameValuePair> params) throws URISyntaxException, IOException, ParseException {
        // 创建URI对象，并且添加查询参数
        URIBuilder uriBuilder = new URIBuilder(url);
        uriBuilder = uriBuilder.addParameters(params);


        // 创建HttpGet请求
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.addHeader("Accept", "text/event-stream");  // 设置接受SSE事件流

        // 创建HttpClient
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // 执行请求
        CloseableHttpResponse response = httpclient.execute(httpGet);

        try {
            // 获取响应的实体流
            HttpEntity entity = response.getEntity();
            // 如果返回的实体内容是流式的
            if (entity != null) {
                // 读取实体流
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                // 持续读取流中的每一行数据
                while ((line = reader.readLine()) != null) {
                    // SSE事件的处理：每个事件行通常以 "data: " 开头
                    if (line.startsWith("data: ")) {
                        String eventData = line.substring(6).trim();  // 提取事件数据部分
                        System.out.println("Received event: " + eventData);
                    }
                }
            }
        } finally {
            httpclient.close();  // 关闭httpclient连接
        }
    }


}