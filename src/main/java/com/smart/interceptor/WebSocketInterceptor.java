package com.smart.interceptor;

import com.smart.Util.JwtUtil;
import com.smart.common.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.List;
import java.util.Map;
@Component
@Slf4j
public class WebSocketInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtProperties jwtProperties;
    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("拦截到资源");
        if (!(serverHttpRequest instanceof ServletServerHttpRequest)) {
            return false;
        }

        //1、从请求头中获取令牌
        HttpHeaders headers = serverHttpRequest.getHeaders();
        List<String> strings = headers.get(jwtProperties.getUserTokenName());
        String token = strings.get(0);
        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            System.out.println("解析成功");
            Integer id = (Integer) claims.get("userId");
            log.info("当前用户为:{}", id);
            /*


              检查是否已经存在session，存在则返回false


            */
            attributes.put("userId", id);
            //然后在这里把用户id放进redis里面，以后需要的时候就在里面找
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
           serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {



    }
}
