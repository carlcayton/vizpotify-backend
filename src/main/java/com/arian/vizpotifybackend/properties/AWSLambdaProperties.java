package com.arian.vizpotifybackend.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
@ConfigurationProperties(prefix = "cloud.function.aws")
public record AWSLambdaProperties(
        String endpoint,
        String region,
        String accessKey,
        String secretKey) {
}
