package com.smart.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "smart.jwt")
@Data
public class JwtProperties {


    private String userSecretKey;
    private String userTokenName;
    private String talkTokenName;
    private long   userTtl;
}
