package com.arian.vizpotifybackend.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JWTProperties {
    private String secretKey;
    private long validityInMs;  // e.g. 3600000 for 1h validity
}