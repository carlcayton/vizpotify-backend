package com.arian.vizpotifybackend.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "redis")
@Data
public class RedisConfigProperties {
    private String host;
    private int port;
}
