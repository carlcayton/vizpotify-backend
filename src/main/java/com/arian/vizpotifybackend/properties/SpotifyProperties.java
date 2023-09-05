package com.arian.vizpotifybackend.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "spotify")
@Data
public class SpotifyProperties {
    private List<String> scopes;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
