package com.smart.interceptor;

import com.smart.Util.JwtUtil;
import com.smart.common.properties.JwtProperties;
import com.smart.constants.SentConstant;
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
import org.springframework.web.socket.WebSocketSession;
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

        //1、从请求参数中获取令牌
        ServletServerHttpRequest sq = (ServletServerHttpRequest) serverHttpRequest;
        String userToken = sq.getServletRequest().getParameter("userToken");
        String talkToken = sq.getServletRequest().getParameter("talkToken");
        /*
        HttpHeaders headers = serverHttpRequest.getHeaders();
        List<String> userTokens = headers.get(jwtProperties.getUserTokenName());
        List<String> talkTokens = headers.get(jwtProperties.getTalkTokenName());
        String userToken = userTokens.get(0);
        String talkToken = talkTokens.get(0);
         */

        //2、校验令牌
        try {
            log.info("用户jwt校验:{}", userToken);
            log.info("对话jwt校验:{}", talkToken);
            Claims userTokenClaims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), userToken);
            Claims talkTokenClaims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), talkToken);
            log.info("解析成功");
            Integer userId = (Integer) userTokenClaims.get("userId");
            Integer talkId = (Integer) talkTokenClaims.get("talkId");

            log.info("当前用户为:{}", userId);
            log.info("当前对话为:{}", talkId);

           if (SentConstant.concurrentHashMap.containsKey(userId)) {
               serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
               return false;
           }
            attributes.put("userId", userId);
            attributes.put("talkId", talkId);
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
