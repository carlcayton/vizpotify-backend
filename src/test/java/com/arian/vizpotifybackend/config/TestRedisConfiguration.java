package com.arian.vizpotifybackend.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.testcontainers.containers.GenericContainer;


@Configuration
@Profile("test")
public class TestRedisConfiguration {

    @Bean
    public GenericContainer redisContainer() {
        GenericContainer container = new GenericContainer<>("redis:latest")
                .withExposedPorts(6379);
        container.start();
        return container;
    }

    @Bean
    public JedisConnectionFactory connectionFactory(GenericContainer redisContainer) {
        Integer mappedPort = redisContainer.getMappedPort(6379);
        String hostAddress = redisContainer.getHost();
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(hostAddress, mappedPort);
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, String> myStringRedisTemplate(JedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, Object> jsonRedisTemplate(JedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jsonSerializer);
        template.afterPropertiesSet();
        return template;
    }
    @Bean
    public Gson testGsonBuilder() {
        return new GsonBuilder()
                .create();
    }
}