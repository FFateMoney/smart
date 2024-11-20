package com.smart.Util;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Component
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


}