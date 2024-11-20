package com.smart.config;

import com.smart.constants.SentConstant;
import com.smart.handler.ChatHandler;
import com.smart.interceptor.WebSocketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.util.Collection;
import java.util.Enumeration;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
    @Autowired
    ChatHandler chatHandler;
    WebSocketInterceptor webSocketInterceptor;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatHandler, "/user/talk").setAllowedOrigins("*")
                .addInterceptors(webSocketInterceptor);
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer(){
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(10000);
        container.setMaxBinaryMessageBufferSize(10000);
        container.setMaxSessionIdleTimeout(600000L);
        return container;
    }
    /*
    @Scheduled(fixedRate = 10000)
    private void sessionHasClosed(){
        Collection<WebSocketSession> sessions = SentConstant.concurrentHashMap.values();
        for(WebSocketSession session : sessions){
            if(!session.isOpen()){
                SentConstant.concurrentHashMap.remove(session.getAttributes().get("userId"));
            }
        }
    }
    */



}